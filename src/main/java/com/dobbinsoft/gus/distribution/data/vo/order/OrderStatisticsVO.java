package com.dobbinsoft.gus.distribution.data.vo.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "订单统计VO")
public class OrderStatisticsVO {
    
    @Schema(description = "待付款订单数量", example = "5")
    private Long unpaidCount;
    
    @Schema(description = "待出库订单数量", example = "3")
    private Long waitStockCount;
    
    @Schema(description = "待收货订单数量", example = "2")
    private Long waitConfirmCount;
    
    @Schema(description = "待评价订单数量", example = "1")
    private Long waitCommentCount;
    
    @Schema(description = "退款中订单数量", example = "0")
    private Long refundingCount;
}
