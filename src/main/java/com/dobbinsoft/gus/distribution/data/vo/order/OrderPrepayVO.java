package com.dobbinsoft.gus.distribution.data.vo.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Schema(description = "订单预支付响应VO")
public class OrderPrepayVO {

    @Schema(description = "订单号", example = "O202412011234567890")
    private String orderNo;

    @Schema(description = "支付参数", example = "{\"appId\":\"wx123456\",\"timeStamp\":\"1234567890\",\"nonceStr\":\"abc123\",\"package\":\"prepay_id=wx123456\",\"signType\":\"RSA\",\"paySign\":\"signature\"}")
    private Object paymentParams;

    @Schema(description = "支付金额（分）", example = "9999")
    private BigDecimal payAmount;

    @Schema(description = "支付提供商ID", example = "provider_123")
    private String providerId;

}
