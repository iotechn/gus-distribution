package com.dobbinsoft.gus.distribution.client.gus.payment.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Schema(description = "退款详情响应")
@Data
public class TransactionRefundVO {
    
    @Schema(description = "退款ID", example = "refund_123")
    private String id;
    
    @Schema(description = "支付提供商ID", example = "provider_123")
    private String providerId;
    
    @Schema(description = "应用名称", example = "my-app")
    private String applicationName;
    
    @Schema(description = "原订单号", example = "ORDER_20231201_001")
    private String orderNo;
    
    @Schema(description = "退款单号", example = "REFUND_20231201_001")
    private String refundNo;
    
    @Schema(description = "微信支付退款单号", example = "50300403322023120100000000001")
    private String refundId;
    
    @Schema(description = "退款状态", example = "SUCCESS")
    private Status status;
    
    @Schema(description = "退款金额", example = "99.99")
    private BigDecimal refundAmount;
    
    @Schema(description = "退款原因", example = "用户申请退款")
    private String reason;
    
    @Schema(description = "退款时间")
    private ZonedDateTime refundTime;
    
    @Schema(description = "创建时间")
    private ZonedDateTime createTime;
    
    @Schema(description = "更新时间")
    private ZonedDateTime updateTime;


    public enum Status {
        PROCESSING,  // 退款处理中
        SUCCESS,     // 退款成功
        FAILED,      // 退款失败
        CLOSED       // 退款关闭
    }

}
