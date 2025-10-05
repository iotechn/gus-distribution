package com.dobbinsoft.gus.distribution.client.gus.payment.model;

import com.dobbinsoft.gus.distribution.data.enums.CurrencyCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Getter
@Setter
public class TransactionUpdateEventDTO {

    private String orderNo;

    private String transactionNo;

    private ProviderVO provider;

    private String providerTradeType;

    private TransactionStatus status;

    private ZonedDateTime paymentTime;

    private CurrencyCode currencyCode;

    private BigDecimal amount;

}
