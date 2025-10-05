package com.dobbinsoft.gus.distribution.data.dto.cart;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@Schema(description = "添加购物车商品DTO")
public class AddCartItemDTO {

    @Schema(description = "SPU唯一标识")
    @NotBlank(message = "SMC不能为空")
    private String smc;

    @Schema(description = "关联SKU")
    @NotBlank(message = "SKU不能为空")
    private String sku;

    @Schema(description = "SKU数量")
    @NotNull(message = "数量不能为空")
    @Min(value = 1, message = "数量必须大于0")
    private Integer quantity;

    @Schema(description = "客制化选项")
    private List<CartItemCustomizationDTO> customizations;

    @Getter
    @Setter
    @Schema(description = "购物车商品客制化选项DTO")
    public static class CartItemCustomizationDTO {
        @Schema(description = "模板ID")
        @NotBlank(message = "模板ID不能为空")
        private String templateId;

        @Schema(description = "选项ID")
        @NotBlank(message = "选项ID不能为空")
        private String optionId;
    }
} 