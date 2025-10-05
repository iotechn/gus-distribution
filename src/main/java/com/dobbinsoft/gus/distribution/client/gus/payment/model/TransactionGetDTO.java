package com.dobbinsoft.gus.distribution.client.gus.payment.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "获取交易详情请求")
@Getter
@Setter
public class TransactionGetDTO {

    @Schema(description = "支付提供商ID", example = "provider_123")
    private String providerId;

    @Schema(description = "应用名称", example = "my-app")
    private String applicationName;

    @Schema(description = "交易ID", example = "transaction_123")
    private String transactionId;

    @Schema(description = "订单号", example = "ORDER_20231201_001")
    private String orderNo;

}
