package com.dobbinsoft.gus.distribution.data.po;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Getter
@Setter
@TableName("ds_order_item")
@Schema(description = "订单商品项实体")
public class OrderItemPO extends BasePO {

    @Schema(description = "订单ID")
    @NotBlank(message = "订单ID不能为空")
    private String orderId;

    @Schema(description = "SMC 名称")
    @NotBlank(message = "商品名称不能为空")
    private String itemName;

    @Schema(description = "SKU 名称")
    @NotBlank(message = "SKU名称不能为空")
    private String skuName;

    @Schema(description = "商品款号")
    @NotBlank(message = "商品款号不能为空")
    private String smc;

    @Schema(description = "SKU")
    @NotBlank(message = "SKU不能为空")
    private String sku;

    @Schema(description = "商品单位")
    private String unit;

    @Schema(description = "商品图片URL")
    private String productPic;

    @Schema(description = "商品缩略图URL")
    private String productPicSmall;

    @Schema(description = "购买数量")
    @NotNull(message = "购买数量不能为空")
    @Positive(message = "购买数量必须大于0")
    private BigDecimal qty;

    @Schema(description = "商品价格")
    @NotNull(message = "商品价格不能为空")
    private BigDecimal price;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "内部备注")
    private String innerRemark;

}
