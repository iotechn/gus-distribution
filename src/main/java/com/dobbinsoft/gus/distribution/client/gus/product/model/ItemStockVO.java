package com.dobbinsoft.gus.distribution.client.gus.product.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "Stock information for a specific item identified by SMC")
public class ItemStockVO {

    @Schema(description = "Standard Merchandise Code (SMC) of the item", example = "ITEM001")
    private String smc;

    @Schema(description = "List of stock records for this item across different locations")
    private List<ListStockVO> stocks;

}
