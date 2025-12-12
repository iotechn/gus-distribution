package com.dobbinsoft.gus.distribution.data.dto.comment;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "创建评论请求")
public class CommentCreateDTO {

    @NotBlank(message = "订单号不能为空")
    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "评论列表")
    private List<CommentItemDTO> comments;

    @Data
    @Schema(description = "评论项")
    public static class CommentItemDTO {

        @NotBlank(message = "商品款号不能为空")
        @Schema(description = "商品款号")
        private String smc;

        @NotBlank(message = "SKU不能为空")
        @Schema(description = "SKU")
        private String sku;

        @NotNull(message = "评分不能为空")
        @Schema(description = "评分(1-5)", minimum = "1", maximum = "5")
        private Integer score;

        @Size(max = 1000, message = "评论内容不能超过1000字符")
        @Schema(description = "评论内容", maxLength = 1000)
        private String content;

        @Schema(description = "图片文件ID列表（通过上传评论图片接口获取）")
        private List<String> fileIds;
    }
}
