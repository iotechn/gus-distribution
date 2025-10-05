package com.dobbinsoft.gus.distribution.data.vo.cart;

import com.dobbinsoft.gus.distribution.client.gus.product.model.ItemVO;
import com.dobbinsoft.gus.distribution.data.enums.StatusType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@Schema(description = "购物车商品VO")
public class CartItemVO {

    @Schema(description = "购物车商品ID")
    private String id;

    @Schema(description = "SPU唯一标识")
    private String smc;

    @Schema(description = "关联SKU")
    private String sku;

    @Schema(description = "SKU数量")
    private Integer quantity;

    @Schema(description = "加入时价格")
    private BigDecimal entryPrice;

    @Schema(description = "购物车Item状态")
    private StatusType status;

    @Schema(description = "商品信息")
    private ItemVO itemInfo;

    @Schema(description = "客制化选项")
    private List<CartItemCustomizationVO> customizations;

    @Schema(description = "创建时间")
    private ZonedDateTime createTime;

    @Schema(description = "更新时间")
    private ZonedDateTime updateTime;

    @Getter
    @Setter
    @Schema(description = "购物车商品客制化选项VO")
    public static class CartItemCustomizationVO {
        @Schema(description = "模板ID")
        private String templateId;

        @Schema(description = "选项ID")
        private String optionId;

        @Schema(description = "模板名称")
        private String templateName;

        @Schema(description = "选项名称")
        private String optionName;
    }
} 