package com.dobbinsoft.gus.distribution.client.gus.product.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "Item Detail VO")
public class ItemDetailVO extends ItemVO {

    @Schema(description = "Specifications")
    private List<ItemSpecificationVO> specifications;

    @Schema(description = "Attributes")
    private List<ItemAttrVO> attrs;


    @Getter
    @Setter
    @Schema(description = "Item Specification VO")
    public static class ItemSpecificationVO {
        @Schema(description = "Specification ID")
        private String id;

        @Schema(description = "Image URL")
        private String imageUrl;

        @Schema(description = "Specification name")
        private String name;

        @Schema(description = "Specification values")
        private List<ItemSpecificationValueVO> values;

    }

    @Getter
    @Setter
    @Schema(description = "Item Attribute VO")
    public static class ItemAttrVO {
        @Schema(description = "Field key")
        private String fieldKey;

        @Schema(description = "Attribute value")
        private String attrValue;

    }

}
