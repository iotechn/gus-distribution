package com.dobbinsoft.gus.distribution.controller.fo;

import com.dobbinsoft.gus.distribution.data.dto.comment.CommentCreateDTO;
import com.dobbinsoft.gus.distribution.data.dto.comment.CommentQueryDTO;
import com.dobbinsoft.gus.distribution.data.vo.comment.CommentVO;
import com.dobbinsoft.gus.distribution.service.CommentService;
import com.dobbinsoft.gus.web.vo.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "评论管理", description = "用户端评论相关接口")
@RestController
@RequestMapping("/fo/comment")
@RequiredArgsConstructor
@Validated
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    @Operation(summary = "创建评论", description = "用户对订单商品进行评论")
    public R<Void> createComment(
            @Valid @RequestBody CommentCreateDTO commentCreateDTO) {
        commentService.createComment(commentCreateDTO);
        return R.success();
    }

    @GetMapping
    @Operation(summary = "查询评论列表", description = "根据条件查询评论列表")
    public R<List<CommentVO>> queryComments(
            @Valid CommentQueryDTO commentQueryDTO) {
        List<CommentVO> comments = commentService.queryComments(commentQueryDTO);
        return R.success(comments);
    }

    // TODO 查询，有必要评价功能吗？
    @GetMapping("/order/{orderNo}")
    @Operation(summary = "根据订单号查询评论", description = "查询指定订单的所有评论")
    public R<List<CommentVO>> getCommentsByOrderNo(
            @Parameter(description = "订单号") @PathVariable String orderNo) {
        List<CommentVO> comments = commentService.getCommentsByOrderNo(orderNo);
        return R.success(comments);
    }

    @GetMapping("/product")
    @Operation(summary = "根据商品查询评论", description = "查询指定商品的评论")
    public R<List<CommentVO>> getCommentsByProduct(
            @Parameter(description = "商品款号") @RequestParam String smc,
            @Parameter(description = "SKU") @RequestParam String sku) {
        List<CommentVO> comments = commentService.getCommentsByProduct(smc, sku);
        return R.success(comments);
    }
}
