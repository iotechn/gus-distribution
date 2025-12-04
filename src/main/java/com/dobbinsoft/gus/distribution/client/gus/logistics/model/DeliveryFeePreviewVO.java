package com.dobbinsoft.gus.distribution.client.gus.logistics.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Schema(description = "配送费预览结果VO")
public class DeliveryFeePreviewVO {

    @Schema(description = "订单金额")
    private BigDecimal orderAmount;

    @Schema(description = "配送距离（米）")
    private Integer distance;

    @Schema(description = "计算出的配送费")
    private BigDecimal calculatedFee;

    @Schema(description = "实际收取的配送费（考虑免费配送和起送价）")
    private BigDecimal actualFee;

    @Schema(description = "是否满足免费配送条件")
    private Boolean isFreeDelivery;

    @Schema(description = "是否满足起送价条件")
    private Boolean meetsDeliveryThreshold;

    @Schema(description = "免费配送门槛")
    private BigDecimal freeThreshold;

    @Schema(description = "起送价门槛")
    private BigDecimal deliveryThreshold;

    @Schema(description = "起步价")
    private BigDecimal baseFee;

    @Schema(description = "起步价有效距离（米）")
    private Integer baseDistance;

    @Schema(description = "续加价")
    private BigDecimal additionalFee;

    @Schema(description = "续加价距离（米）")
    private Integer additionalDistance;

    @Schema(description = "使用的配送费策略ID")
    private String strategyId;

    @Schema(description = "使用的配送费策略门店Code")
    private String strategyLocationCode;

    @Schema(description = "计算说明")
    private String calculationDescription;
}
