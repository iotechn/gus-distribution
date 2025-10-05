package com.dobbinsoft.gus.distribution.data.vo.cart;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@Schema(description = "购物车VO")
public class CartVO {

    @Schema(description = "购物车ID")
    private String id;

    @Schema(description = "购物车引用类型")
    private Integer referType;

    @Schema(description = "购物车引用ID")
    private String referId;

    @Schema(description = "购物车内商品总数量")
    private Integer quantity;

    @Schema(description = "购物车商品列表")
    private List<CartItemVO> items;

    @Schema(description = "创建时间")
    private ZonedDateTime createTime;

    @Schema(description = "更新时间")
    private ZonedDateTime updateTime;
} 