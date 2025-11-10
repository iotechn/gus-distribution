package com.dobbinsoft.gus.distribution.data.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "订单提交DTO")
public class OrderSubmitDTO {

    @Schema(description = "收货地址ID", example = "123456")
    @NotBlank(message = "收货地址ID不能为空")
    private String addressId;

    @Schema(description = "订单备注", example = "请尽快发货")
    @Size(max = 500, message = "订单备注长度不能超过500个字符")
    private String remark;

    @Schema(description = "购物车商品ID列表", example = "[\"cart_item_1\", \"cart_item_2\"]")
    @Size(max = 100, message = "购物车商品数量不能超过100个")
    private List<Long> cartItemIds;

    @Schema(description = "直接提交的商品列表")
    @Size(max = 100, message = "商品数量不能超过100个")
    @Valid
    private List<OrderItem> orderItems;

    @Getter
    @Setter
    @Schema(description = "订单商品信息")
    public static class OrderItem {

        @Schema(description = "SKU ID", example = "SKU001")
        @NotEmpty(message = "SKU ID不能为空")
        @Size(max = 100, message = "SKU ID长度不能超过100个字符")
        private String skuId;

        @Schema(description = "购买数量", example = "2")
        @NotNull(message = "购买数量不能为空")
        @Positive(message = "购买数量必须为正数")
        private Integer qty;

    }

}
