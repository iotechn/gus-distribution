package com.dobbinsoft.gus.distribution.client.gus.product.model;

import com.dobbinsoft.gus.common.model.dto.PageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "Data Transfer Object for stock search with pagination and multiple filter criteria")
public class StockSearchDTO extends PageDTO {

    @Schema(description = "List of location codes to filter stocks", example = "[\"WH001\", \"WH002\"]")
    private List<String> locationCode;

    @Schema(description = "List of SKU codes to filter stocks", example = "[\"SKU001\", \"SKU002\"]")
    private List<String> sku;

    @Schema(description = "List of SMC codes to filter stocks", example = "[\"ITEM001\", \"ITEM002\"]")
    private List<String> smc;

}
