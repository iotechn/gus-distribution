package com.dobbinsoft.gus.distribution.client.gus.logistics.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@Schema(description = "配送订单VO")
public class DeliveryOrderVO {

    @Schema(description = "订单ID")
    private String id;
    
    @Schema(description = "下单应用名")
    private String applicationName;
    
    @Schema(description = "订单号")
    private String orderNo;
    
    @Schema(description = "配送单号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String deliveryNo;
    
    @Schema(description = "配送单状态", requiredMode = Schema.RequiredMode.REQUIRED)
    private Status status;

    @Schema(description = "收件人")
    private Address receiver;

    @Schema(description = "发件人")
    private Address sender;

    @Schema(description = "门店Code，冗余方便后期扩展 / 问题排查")
    private String locationCode;

    @Schema(description = "配送距离", example = "1000")
    private Integer deliveryDistance;
    
    @Schema(description = "配送费", example = "500")
    private BigDecimal deliveryAmount;
    
    @Schema(description = "配送单备注")
    private String remark;
    
    @Schema(description = "是否需要取件码", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean enablePickupCode;
    
    @Schema(description = "取件码")
    private String pickupCode;
    
    @Schema(description = "取件时间")
    private ZonedDateTime pickupTime;
    
    @Schema(description = "是否需要签收码", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean enableSignCode;
    
    @Schema(description = "签收码")
    private String signCode;
    
    @Schema(description = "签收时间")
    private ZonedDateTime signTime;

    @Schema(description = "配送员信息（当状态为TO_PICKUP或TO_SIGN时返回）")
    private DeliveryOrderDeliverymanVO deliveryman;

    @Schema(description = "配送员轨迹信息（当状态为TO_PICKUP或TO_SIGN时返回）")
    private List<DeliverymanTrackVO> deliverymanTracks;

    @Schema(description = "配送员接单时间")
    private ZonedDateTime distributionTime;

    @Getter
    @Setter
    public static class Address {

        @Schema(description = "名称", requiredMode = Schema.RequiredMode.REQUIRED)
        private String name;

        @Schema(description = "手机号")
        private String mobile;

        @Schema(description = "地址", requiredMode = Schema.RequiredMode.REQUIRED)
        private String address;

        @Schema(description = "经度", example = "116.397128")
        private BigDecimal longitude;

        @Schema(description = "纬度", example = "39.916527")
        private BigDecimal latitude;
    }


    @Getter
    public enum Status {
        // 待打包
        TO_PACK,
        // 待分配 / 待接单
        TO_DISTRIBUTION,
        // 待取件
        TO_PICKUP,
        // 待签收
        TO_SIGN,
        // 已完成
        COMPLETED,
        ;
    }

}
