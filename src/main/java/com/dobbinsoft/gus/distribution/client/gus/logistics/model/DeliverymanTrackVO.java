package com.dobbinsoft.gus.distribution.client.gus.logistics.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 配送员轨迹VO
 */
@Data
public class DeliverymanTrackVO {

    /**
     * 轨迹ID
     */
    private String id;

    /**
     * 配送员ID
     */
    private String deliverymanId;

    /**
     * 经度
     */
    private BigDecimal longitude;

    /**
     * 纬度
     */
    private BigDecimal latitude;

    /**
     * 上报时间
     */
    private LocalDateTime reportTime;

    /**
     * 速度（米/秒）
     */
    private BigDecimal speed;

    /**
     * 方向角（度）
     */
    private BigDecimal bearing;

    /**
     * 精度（米）
     */
    private BigDecimal accuracy;

    /**
     * 海拔（米）
     */
    private BigDecimal altitude;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

}
