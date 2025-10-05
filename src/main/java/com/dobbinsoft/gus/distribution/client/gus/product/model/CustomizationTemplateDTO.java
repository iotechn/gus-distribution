package com.dobbinsoft.gus.distribution.client.gus.product.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Schema(description = "Customization template sync DTO for search engine")
public class CustomizationTemplateDTO {

    @Schema(description = "Template ID")
    private String id;

    @Schema(description = "Template name")
    private String name;

    @Schema(description = "Template description")
    private String description;

    @Schema(description = "Selection type")
    private CustomizationTemplateSelectionType selectionType;

    @Schema(description = "Whether this template is required")
    private Boolean required;

    @Schema(description = "Sort weight")
    private Integer sortWeight;

    @Schema(description = "List of customization options")
    private List<CustomizationOptionDTO> options;


    @Getter
    @Setter
    @Schema(description = "Customization option sync DTO")
    public static class CustomizationOptionDTO {
        @Schema(description = "Option ID")
        private String id;

        @Schema(description = "Option name")
        private String name;

        @Schema(description = "Option description")
        private String description;

        @Schema(description = "Additional price")
        private BigDecimal additionalPrice;

        @Schema(description = "Currency code")
        private String currencyCode;

        @Schema(description = "Sort weight")
        private Integer sortWeight;

        @Schema(description = "Whether this option is selected by default")
        private Boolean defaultSelected;

    }
} 