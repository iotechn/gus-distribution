package com.dobbinsoft.gus.distribution.client.erp.model.jdy;

import java.math.BigDecimal;

import lombok.Data;

/**
 * ERP库存项
 */
@Data
public class JdyErpInventoryItem {
    /**
     * 商品编码
     */
    private String productNumber;

    /**
     * 库存数量
     */
    private BigDecimal qty;
} 