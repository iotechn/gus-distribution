package com.dobbinsoft.gus.distribution.client.gus.payment.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "获取退款详情请求")
@Getter
@Setter
public class TransactionRefundGetDTO {

    @NotBlank
    @Schema(description = "支付提供商ID", example = "provider_123")
    private String providerId;

    @NotBlank
    @Schema(description = "应用名称", example = "my-app")
    private String applicationName;

    @Schema(description = "退款单号", example = "REFUND_20231201_001")
    private String refundNo;

    @Schema(description = "原订单号", example = "ORDER_20231201_001")
    private String orderNo;

    @Schema(description = "微信支付退款单号", example = "50300403322023120100000000001")
    private String refundId;

}
