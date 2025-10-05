package com.dobbinsoft.gus.distribution.controller.fo;

import com.dobbinsoft.gus.web.vo.R;
import com.dobbinsoft.gus.distribution.data.dto.cart.AddCartItemDTO;
import com.dobbinsoft.gus.distribution.data.vo.cart.CartVO;
import com.dobbinsoft.gus.distribution.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "购物车管理", description = "用户端购物车相关接口")
@RestController
@RequestMapping("/fo/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Operation(summary = "添加商品到购物车")
    @PostMapping("/add")
    public R<Void> addCartItem(@Valid @RequestBody AddCartItemDTO addCartItemDTO) {
        cartService.addCartItem(addCartItemDTO);
        return R.success();
    }

    @Operation(summary = "获取用户购物车列表")
    @GetMapping("/list")
    public R<CartVO> getUserCart() {
        CartVO cartVO = cartService.getUserCart();
        return R.success(cartVO);
    }

    @Operation(summary = "统计购物车商品数量")
    @GetMapping("/count")
    public R<Integer> getCartItemCount() {
        Integer count = cartService.getCartItemCount();
        return R.success(count);
    }

    @Operation(summary = "移除购物车商品")
    @DeleteMapping("/item/{cartItemId}")
    public R<Void> removeCartItem(@PathVariable String cartItemId) {
        cartService.removeCartItem(cartItemId);
        return R.success();
    }

    @Operation(summary = "更新购物车商品数量")
    @PutMapping("/item/{cartItemId}/quantity")
    public R<Void> updateCartItemQuantity(@PathVariable String cartItemId, @RequestParam Integer quantity) {
        cartService.updateCartItemQuantity(cartItemId, quantity);
        return R.success();
    }
}
