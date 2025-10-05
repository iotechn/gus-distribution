package com.dobbinsoft.gus.distribution.data.po;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
@TableName("ec_cart_item_customization")
@Schema(description = "购物车商品项客制化实体")
public class CartItemCustomizationPO extends BasePO {

    @Schema(description = "购物车商品项ID")
    @NotBlank(message = "购物车商品项ID不能为空")
    private String cartItemId;

    @Schema(description = "模板ID")
    @NotBlank(message = "模板ID不能为空")
    private String templateId;

    @Schema(description = "选项ID")
    @NotBlank(message = "选项ID不能为空")
    private String optionId;

}
