package com.dobbinsoft.gus.distribution.data.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

@Data
@Schema(description = "创建评论请求")
public class CommentCreateDTO {

    @NotBlank(message = "订单号不能为空")
    @Schema(description = "订单号", required = true)
    private String orderNo;

    @Schema(description = "评论列表")
    private List<CommentItemDTO> comments;

    @Data
    @Schema(description = "评论项")
    public static class CommentItemDTO {

        @NotBlank(message = "商品款号不能为空")
        @Schema(description = "商品款号", required = true)
        private String smc;

        @NotBlank(message = "SKU不能为空")
        @Schema(description = "SKU", required = true)
        private String sku;

        @NotNull(message = "评分不能为空")
        @Schema(description = "评分(1-5)", required = true, minimum = "1", maximum = "5")
        private Integer score;

        @Size(max = 1000, message = "评论内容不能超过1000字符")
        @Schema(description = "评论内容", maxLength = 1000)
        private String content;

        @Schema(description = "图片URL列表")
        private List<String> imageUrls;
    }
}
