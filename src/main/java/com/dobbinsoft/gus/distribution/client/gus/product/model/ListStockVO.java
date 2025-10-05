package com.dobbinsoft.gus.distribution.client.gus.product.model;

import com.dobbinsoft.gus.distribution.data.enums.CurrencyCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Schema(description = "Stock record information for list display")
public class ListStockVO {

    @Schema(description = "Primary key in format: locationCode_sku", example = "WH001_SKU001")
    private String locationSku;

    @Schema(description = "Warehouse location code", example = "WH001")
    private String locationCode;

    @Schema(description = "SKU code of the product", example = "SKU001")
    private String sku;

    @Schema(description = "Standard Merchandise Code (SMC) of the item", example = "ITEM001")
    private String smc;

    @Schema(description = "Currency code for pricing", example = "USD")
    private CurrencyCode currencyCode;

    @Schema(description = "Price of the product", example = "99.99")
    private BigDecimal price;

    @Schema(description = "Available stock quantity", example = "100")
    private BigDecimal quantity;

}
