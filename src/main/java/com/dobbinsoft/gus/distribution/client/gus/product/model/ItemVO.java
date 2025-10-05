package com.dobbinsoft.gus.distribution.client.gus.product.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@Schema(description = "Item VO")
public class ItemVO {

    @Schema(description = "Item ID")
    private Long id;

    @Schema(description = "SMC")
    private String smc;

    @Schema(description = "Status")
    private ItemStatus status;

    @Schema(description = "Stockable")
    private Boolean stockable;

    @Schema(description = "Rich text")
    private String richText;

    @Schema(description = "Images")
    private List<String> images;

    @Schema(description = "Categories")
    private List<ItemCategory> categories;

    @Schema(description = "Unit group")
    private UnitGroupVO unitGroup;

    @Schema(description = "SKUs")
    private List<ItemSkuVO> skus;

    @Schema(description = "Details")
    private List<ItemDetailInfoVO> details;

    @Schema(description = "Customization Templates")
    private List<CustomizationTemplateDTO> customizationTemplates;

    @Schema(description = "Created time")
    private ZonedDateTime createTime;

    @Schema(description = "Updated time")
    private ZonedDateTime updateTime;

    @Getter
    @Setter
    @Schema(description = "Item Category")
    public static class ItemCategory {
        @Schema(description = "Category ID")
        private Long id;

        @Schema(description = "Category name")
        private String name;

        public ItemCategory(Long id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    @Getter
    @Setter
    @Schema(description = "Item SKU VO")
    public static class ItemSkuVO {
        @Schema(description = "SKU ID")
        private Long id;

        @Schema(description = "SKU code")
        private String sku;

        @Schema(description = "Created time")
        private ZonedDateTime createTime;

        @Schema(description = "Updated time")
        private ZonedDateTime updateTime;

    }


    @Getter
    @Setter
    @Schema(description = "Item Detail Info VO")
    public static class ItemDetailInfoVO {
        @Schema(description = "Language")
        private String language;

        @Schema(description = "Name")
        private String name;

        @Schema(description = "Description")
        private String description;

        @Schema(description = "Rich text")
        private String richText;

    }
}
