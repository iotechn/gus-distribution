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
@TableName("ds_order_refund_item")
@Schema(description = "订单退款项实体")
public class OrderRefundItemPO extends BasePO {

    @Schema(description = "退款单ID")
    @NotBlank(message = "退款单ID不能为空")
    private String refundId;

    @Schema(description = "订单商品项ID")
    @NotBlank(message = "订单商品项ID不能为空")
    private String orderItemId;

    @Schema(description = "商品名称")
    @NotBlank(message = "商品名称不能为空")
    private String itemName;

    @Schema(description = "SKU名称")
    @NotBlank(message = "SKU名称不能为空")
    private String skuName;

    @Schema(description = "商品款号")
    @NotBlank(message = "商品款号不能为空")
    private String smc;

    @Schema(description = "SKU")
    @NotBlank(message = "SKU不能为空")
    private String sku;

    @Schema(description = "退款数量")
    @NotNull(message = "退款数量不能为空")
    @Positive(message = "退款数量必须大于0")
    private Integer refundQty;

    @Schema(description = "商品单价")
    @NotNull(message = "商品单价不能为空")
    private BigDecimal price;

    @Schema(description = "退款金额")
    @NotNull(message = "退款金额不能为空")
    private BigDecimal refundAmount;

    @Schema(description = "备注")
    private String remark;
}