package com.dobbinsoft.gus.distribution.controller.fo;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dobbinsoft.gus.common.model.vo.PageResult;
import com.dobbinsoft.gus.distribution.client.gus.file.FileFeignClient;
import com.dobbinsoft.gus.distribution.client.gus.file.model.FileItemVO;
import com.dobbinsoft.gus.distribution.data.dto.comment.CommentCreateDTO;
import com.dobbinsoft.gus.distribution.data.dto.comment.CommentQueryDTO;
import com.dobbinsoft.gus.distribution.data.vo.comment.CommentVO;
import com.dobbinsoft.gus.distribution.service.CommentService;
import com.dobbinsoft.gus.web.exception.BasicErrorCode;
import com.dobbinsoft.gus.web.exception.ServiceException;
import com.dobbinsoft.gus.web.vo.R;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "评论管理", description = "用户端评论相关接口")
@RestController
@RequestMapping("/fo/comment")
@RequiredArgsConstructor
@Validated
public class CommentController {

    private final CommentService commentService;
    private final FileFeignClient fileFeignClient;

    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp", "image/bmp"
    );
    private static final int COMMENT_IMAGE_EXPIRY_SECONDS = 7200; // 2小时

    @PostMapping("/image/upload")
    @Operation(summary = "上传评论图片", description = "上传评论图片，返回文件信息（临时文件，2小时有效）")
    public R<FileItemVO> uploadCommentImage(
            @Parameter(description = "图片文件") @RequestParam("file") MultipartFile file) {
        // 校验文件是否为空
        if (file == null || file.isEmpty()) {
            throw new ServiceException(BasicErrorCode.PARAMERROR, "文件不能为空");
        }

        // 校验文件类型是否为图片
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            throw new ServiceException(BasicErrorCode.PARAMERROR, "只能上传图片文件（支持 jpg、png、gif、webp、bmp 格式）");
        }

        // 生成带UUID的文件名，防止重复，保留后缀
        String originalFilename = file.getOriginalFilename();
        String customFileName = generateFileNameWithUuid(originalFilename);

        // 上传文件到文件服务，设置为临时文件，2小时后过期
        R<FileItemVO> uploadResult = fileFeignClient.uploadFile(
                file,
                false, // 私有文件
                "comment", // 文件夹
                COMMENT_IMAGE_EXPIRY_SECONDS, // 2小时过期
                customFileName // 自定义文件名（带UUID）
        );

        if (!BasicErrorCode.SUCCESS.getCode().equals(uploadResult.getCode())) {
            throw new ServiceException(uploadResult.getCode(), uploadResult.getMessage());
        }

        return R.success(uploadResult.getData());
    }

    @PostMapping
    @Operation(summary = "创建评论", description = "用户对订单商品进行评论，需要传入之前上传的图片文件ID")
    public R<Void> createComment(
            @Valid @RequestBody CommentCreateDTO commentCreateDTO) {
        commentService.createComment(commentCreateDTO);
        return R.success();
    }

    @GetMapping
    @Operation(summary = "查询评论列表", description = "根据条件分页查询评论列表")
    public R<PageResult<CommentVO>> queryComments(
            @Valid CommentQueryDTO commentQueryDTO) {
        PageResult<CommentVO> page = commentService.queryComments(commentQueryDTO);
        return R.success(page);
    }

    @GetMapping("/order/{orderNo}")
    @Operation(summary = "根据订单号查询评论", description = "查询指定订单的所有评论")
    public R<List<CommentVO>> getCommentsByOrderNo(
            @Parameter(description = "订单号") @PathVariable String orderNo) {
        List<CommentVO> comments = commentService.getCommentsByOrderNo(orderNo);
        return R.success(comments);
    }

    @GetMapping("/product")
    @Operation(summary = "根据商品查询评论", description = "分页查询指定商品的评论")
    public R<PageResult<CommentVO>> getCommentsByProduct(
            @Parameter(description = "商品款号") @RequestParam(required = false) String smc,
            @Parameter(description = "SKU") @RequestParam(required = false) String sku,
            @Parameter(description = "页码") @RequestParam Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam Integer pageSize) {
        if (StringUtils.isEmpty(smc) && StringUtils.isEmpty(sku)) {
            throw new ServiceException(BasicErrorCode.PARAMERROR);
        }
        PageResult<CommentVO> page = commentService.getCommentsByProduct(smc, sku, pageNum, pageSize);
        return R.success(page);
    }

    /**
     * 生成带UUID的文件名，防止重复
     * 格式：原文件名（不含后缀）_UUID.后缀
     * 例如：image.jpg -> image_550e8400-e29b-41d4-a716-446655440000.jpg
     *
     * @param originalFilename 原始文件名
     * @return 带UUID的文件名
     */
    private String generateFileNameWithUuid(String originalFilename) {
        if (StringUtils.isBlank(originalFilename)) {
            return UUID.randomUUID().toString();
        }

        // 查找最后一个点号的位置（文件后缀）
        int lastDotIndex = originalFilename.lastIndexOf('.');
        
        if (lastDotIndex == -1) {
            // 没有后缀，直接添加UUID
            return originalFilename + "_" + UUID.randomUUID().toString();
        }

        // 分离文件名和后缀
        String nameWithoutExt = originalFilename.substring(0, lastDotIndex);
        String extension = originalFilename.substring(lastDotIndex);

        // 拼接：原文件名_UUID.后缀
        return nameWithoutExt + "_" + UUID.randomUUID().toString() + extension;
    }
}
