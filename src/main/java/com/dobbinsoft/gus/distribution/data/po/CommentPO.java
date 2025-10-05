package com.dobbinsoft.gus.distribution.data.po;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

@Getter
@Setter
@TableName("ec_comment")
@Schema(description = "评论实体")
public class CommentPO extends BasePO {

    @Schema(description = "商品款号")
    @NotBlank(message = "商品款号不能为空")
    private String smc;

    @Schema(description = "SKU")
    @NotBlank(message = "SKU不能为空")
    private String sku;

    @Schema(description = "用户ID")
    @NotBlank(message = "用户ID不能为空")
    private String userId;

    @Schema(description = "订单号")
    @NotBlank(message = "订单号不能为空")
    private String orderNo;

    @Schema(description = "评论内容")
    @NotBlank(message = "评论内容不能为空")
    private String content;

    @Schema(description = "评分", example = "5")
    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分最小为1")
    @Max(value = 5, message = "评分最大为5")
    private Integer score;

    @Schema(description = "是否有图片")
    @NotNull(message = "是否有图片不能为空")
    private Boolean hasImage;
}
