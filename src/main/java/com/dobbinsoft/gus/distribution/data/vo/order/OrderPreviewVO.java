package com.dobbinsoft.gus.distribution.data.vo.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Schema(description = "订单预览VO")
public class OrderPreviewVO {

    @Schema(description = "物流费用", example = "0.00")
    private BigDecimal deliveryAmount;

    @Schema(description = "配送距离（米）")
    private Integer deliveryDistance;

    @Schema(description = "订单总金额", example = "100.00")
    private BigDecimal totalAmount;

}
