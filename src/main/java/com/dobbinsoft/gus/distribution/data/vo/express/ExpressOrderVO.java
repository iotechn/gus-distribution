package com.dobbinsoft.gus.distribution.data.vo.express;

import com.dobbinsoft.gus.distribution.data.enums.LpCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ExpressOrderVO {

    private String id;

    @Schema(description = "")
    private String applicationName;

    @Schema(description = "承运商编码")
    private LpCode lpCode;

    @Schema(description = "承运商名称")
    private String lpName;

    @Schema(description = "运单号")
    private String transNo;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "发件人")
    private Sender sender;

    @Schema(description = "收件人")
    private Receiver receiver;

    @Schema(description = "物流轨迹列表")
    private List<Trace> traceList;

    @Getter
    @Setter
    public static class Sender {
        private String name;
        private String mobile;
        private String telephone;
        private String country;
        private String province;
        private String city;
        private String district;
        private String address;
        private String zipcode;
    }

    @Getter
    @Setter
    public static class Receiver {
        private String name;
        private String buyerName;
        private String mobile;
        private String telephone;
        private String country;
        private String province;
        private String city;
        private String district;
        private String address;
        private String zipcode;
        private BigDecimal receiverLongitude;
        private BigDecimal receiverLatitude;
    }

    @Getter
    @Setter
    public static class Trace {
        @Schema(description = "轨迹更新时间")
        private LocalDateTime createTime;
        
        @Schema(description = "轨迹描述")
        private String context;
        
        @Schema(description = "当时快递单状态")
        private Status status;
        
        @Schema(description = "所在地区名")
        private String areaName;
        
        @Schema(description = "所在地区经度")
        private BigDecimal areaLongitude;
        
        @Schema(description = "所在地区纬度")
        private BigDecimal areaLatitude;
    }
    
    public enum Status {
        // 在途
        IN_TRANSIT,
        // 揽件
        PICKED_UP,
        // 异常
        EXCEPTION,
        // 签收
        DELIVERED,
        // 退签
        RETURNED,
        // 派送
        OUT_FOR_DELIVERY,
        // 清关
        CUSTOMS_CLEARANCE,
        // 拒签
        REJECTED
    }
} 