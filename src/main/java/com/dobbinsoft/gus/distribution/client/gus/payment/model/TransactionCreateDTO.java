package com.dobbinsoft.gus.distribution.client.gus.payment.model;

import com.dobbinsoft.gus.distribution.data.enums.CurrencyCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "创建交易请求")
@Getter
@Setter
public class TransactionCreateDTO {

    @NotBlank
    @Schema(description = "支付提供商ID", example = "provider_123")
    private String providerId;

    @NotBlank
    @Schema(description = "订单号", example = "ORDER_20231201_001")
    private String orderNo;

    @Schema(description = "用户OpenID", example = "oUpF8uMuAJO_M2pxb1Q9zNjvS")
    private String openId;

    @NotNull
    @Schema(description = "货币代码", example = "CNY")
    private CurrencyCode currencyCode;

    @NotNull
    @Schema(description = "支付金额", example = "99.99")
    private BigDecimal amount;

    @Schema(description = "商品列表")
    private List<Item> items;


    @Schema(description = "商品信息")
    @Getter
    @Setter
    public static class Item {
        @Schema(description = "商品SKU", example = "SKU_001")
        private String sku;
        @Schema(description = "商品名称", example = "iPhone 15")
        private String name;
        @Schema(description = "商品数量", example = "1")
        private BigDecimal quantity;
        @Schema(description = "商品单价", example = "99.99")
        private BigDecimal price;
    }

}
