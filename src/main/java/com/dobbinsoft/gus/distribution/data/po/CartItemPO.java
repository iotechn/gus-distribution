package com.dobbinsoft.gus.distribution.data.po;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Getter
@Setter
@TableName("ec_cart_item")
@Schema(description = "购物车商品项实体")
public class CartItemPO extends BasePO{

    @Schema(description = "关联购物车ID")
    @NotBlank(message = "购物车ID不能为空")
    private String cartId;

    @Schema(description = "购物车Item状态", example = "1")
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(description = "SPU唯一标识")
    @NotBlank(message = "SPU标识不能为空")
    private String smc;

    @Schema(description = "关联SKU")
    @NotBlank(message = "SKU不能为空")
    private String sku;

    @Schema(description = "SKU数量")
    @NotNull(message = "数量不能为空")
    @Positive(message = "数量必须大于0")
    private Integer quantity;

    @Schema(description = "加入时价格")
    @NotNull(message = "价格不能为空")
    private BigDecimal entryPrice;

    @Schema(description = "代表客制化选项相同的值")
    private String customizationSignature;

}
