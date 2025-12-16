package com.dobbinsoft.gus.distribution.client.erp.model;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErpStock {

    private String locationCode;

    private String sku;

    private BigDecimal quantity;

}
