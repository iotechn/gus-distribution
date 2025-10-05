package com.dobbinsoft.gus.distribution.data.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

// 移除BigDecimal导入，因为不再需要

@Getter
@Setter
@Schema(description = "订单退款项DTO")
public class OrderRefundItemDTO {

    @Schema(description = "订单商品项ID", example = "order_item_123")
    @NotBlank(message = "订单商品项ID不能为空")
    private String orderItemId;

    @Schema(description = "退款数量", example = "1")
    @NotNull(message = "退款数量不能为空")
    @Positive(message = "退款数量必须大于0")
    private Integer refundQty;

    // 退款金额由后端根据OrderItem的单价计算，不需要前端传入
    
    @Schema(description = "备注", example = "商品有瑕疵")
    private String remark;
}