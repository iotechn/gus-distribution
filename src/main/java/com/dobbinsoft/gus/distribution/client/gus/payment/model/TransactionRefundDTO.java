package com.dobbinsoft.gus.distribution.client.gus.payment.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Schema(description = "交易退款请求")
@Getter
@Setter
public class TransactionRefundDTO {

    @NotBlank
    @Schema(description = "支付提供商ID", example = "provider_123")
    private String providerId;

    @NotBlank
    @Schema(description = "应用名称", example = "my-app")
    private String applicationName;

    @NotBlank
    @Schema(description = "原订单号", example = "ORDER_20231201_001")
    private String orderNo;

    @NotBlank
    @Schema(description = "退款单号", example = "REFUND_20231201_001")
    private String refundNo;

    @NotNull
    @Schema(description = "退款金额", example = "99.99")
    private BigDecimal refundAmount;

    @Schema(description = "退款原因", example = "用户申请退款")
    private String reason;

    @Schema(description = "退款通知地址", example = "https://example.com/refund-callback")
    private String notifyUrl;

}
