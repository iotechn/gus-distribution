package com.dobbinsoft.gus.distribution.data.vo.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 订单退款项VO
 */
@Getter
@Setter
public class OrderRefundItemVO {

    @Schema(description = "退款项ID", example = "refund_item_123")
    private String id;
    
    @Schema(description = "退款单ID", example = "refund_123")
    private String refundId;

    @Schema(description = "订单商品项ID", example = "order_item_123")
    private String orderItemId;

    @Schema(description = "商品名称", example = "iPhone 13")
    private String itemName;

    @Schema(description = "SKU名称", example = "iPhone 13 128G 黑色")
    private String skuName;

    @Schema(description = "商品款号", example = "IPHONE13")
    private String smc;

    @Schema(description = "SKU", example = "IPHONE13-128G-BLACK")
    private String sku;

    @Schema(description = "退款数量", example = "1")
    private Integer refundQty;

    @Schema(description = "商品单价", example = "6999.00")
    private BigDecimal price;

    @Schema(description = "退款金额", example = "6999.00")
    private BigDecimal refundAmount;

    @Schema(description = "备注", example = "商品有瑕疵")
    private String remark;
}