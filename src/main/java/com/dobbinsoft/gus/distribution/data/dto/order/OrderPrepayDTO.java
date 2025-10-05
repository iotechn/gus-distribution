package com.dobbinsoft.gus.distribution.data.dto.order;

import com.dobbinsoft.gus.distribution.data.enums.CurrencyCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "订单预支付请求DTO")
public class OrderPrepayDTO {

    @Schema(description = "订单号", example = "O202412011234567890")
    @NotBlank(message = "订单号不能为空")
    private String orderNo;

    @Schema(description = "支付提供商ID", example = "provider_123")
    @NotBlank(message = "支付提供商ID不能为空")
    private String providerId;

    @Schema(description = "支付币种")
    private CurrencyCode currencyCode;

}
