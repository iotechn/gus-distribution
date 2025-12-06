package com.dobbinsoft.gus.distribution.client.gus.product.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class BatchStockAdjustDTO {

    private List<ItemDTO> items;


    @Getter
    @Setter
    public static class ItemDTO {

        @Schema(contentMediaType = "location code")
        @NotBlank(message = "Location code cannot be blank")
        private String locationCode;

        @Schema(description = "sku")
        @NotBlank(message = "sku cannot be blank")
        private String sku;

        @Schema(description = "operation type")
        @NotNull(message = "Operation cannot be null")
        private Operation operation;

        @NotNull(message = "Quantity cannot be null")
        @Positive(message = "Quantity must be positive")
        private BigDecimal quantity;

    }

    public enum Operation {
        ADD,
        SUBTRACT,
    }

}
