package com.dobbinsoft.gus.distribution.client.gus.payment.model;

import com.dobbinsoft.gus.distribution.data.enums.CurrencyCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@Schema(description = "交易详情响应")
@Data
public class TransactionVO {
    
    @Schema(description = "交易ID", example = "transaction_123")
    private String id;
    
    @Schema(description = "支付提供商ID", example = "provider_123")
    private String providerId;
    
    @Schema(description = "应用名称", example = "my-app")
    private String applicationName;
    
    @Schema(description = "订单号", example = "ORDER_20231201_001")
    private String orderNo;
    
    @Schema(description = "支付者OpenID", example = "oUpF8uMuAJO_M2pxb1Q9zNjvS")
    private String openId;
    
    @Schema(description = "交易状态", example = "SUCCESS")
    private TransactionStatus status;
    
    @Schema(description = "支付平台交易号", example = "4200001234567890")
    private String transactionNo;

    @Schema(description = "币种", example = "CNY")
    private CurrencyCode currencyCode;
    
    @Schema(description = "支付金额", example = "99.99")
    private BigDecimal amount;
    
    @Schema(description = "支付时间")
    private ZonedDateTime paymentTime;
    
    @Schema(description = "交易商品列表")
    private List<Item> items;
    
    @Schema(description = "交易商品")
    @Data
    public static class Item {
        private String sku;
        private String name;
        private BigDecimal quantity;
        private BigDecimal price;
    }
}
