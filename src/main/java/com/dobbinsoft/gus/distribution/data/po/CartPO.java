package com.dobbinsoft.gus.distribution.data.po;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@TableName("ec_cart")
@Schema(description = "购物车实体")
public class CartPO extends BasePO {

    /**
     * com.dobbinsoft.gus.distribution.data.enums.CartReferType
     */
    @Schema(description = "购物车引用类型", example = "1")
    @NotNull(message = "引用类型不能为空")
    private Integer referType;

    @Schema(description = "购物车引用ID")
    @NotBlank(message = "引用ID不能为空")
    private String referId;

    @Schema(description = "购物车内商品总数量")
    @NotNull(message = "商品总数量不能为空")
    private Integer quantity;

}
