package com.dobbinsoft.gus.distribution.service;

import com.dobbinsoft.gus.distribution.data.dto.cart.AddCartItemDTO;
import com.dobbinsoft.gus.distribution.data.vo.cart.CartVO;

public interface CartService {

    /**
     * 添加商品到购物车
     */
    void addCartItem(AddCartItemDTO addCartItemDTO);

    /**
     * 获取用户购物车列表
     */
    CartVO getUserCart();

    /**
     * 统计购物车商品数量
     */
    Integer getCartItemCount();

    /**
     * 移除购物车商品
     */
    void removeCartItem(String cartItemId);

    /**
     * 更新购物车商品数量
     */
    void updateCartItemQuantity(String cartItemId, Integer quantity);
} 