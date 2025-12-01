package com.dobbinsoft.gus.distribution.client.gus.product.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Item Specification Value VO")
public class ItemSpecificationValueVO {

    @Schema(description = "Value ID")
    private String id;

    @Schema(description = "Value name")
    private String name;

    @Schema(description = "Specification value")
    private String specificationValue;

}