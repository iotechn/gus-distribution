package com.dobbinsoft.gus.distribution.data.vo.item;

import com.dobbinsoft.gus.distribution.client.gus.product.model.ItemDetailVO;
import com.dobbinsoft.gus.distribution.client.gus.product.model.ItemVO;
import com.dobbinsoft.gus.distribution.data.enums.CurrencyCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Schema(description = "带有库存信息的商品 VO")
public class ItemWithStockVO extends ItemDetailVO {

    @Schema(description = "请求携带的仓库编码")
    private String locationCode;

    @Schema(description = "指定仓库的库存信息列表")
    private List<LocationStock> locationStocks;

    @Schema(description = "最便宜的 SKU 价格")
    private BigDecimal minPrice;

    @Schema(description = "最贵的 SKU 价格")
    private BigDecimal maxPrice;

    @Getter
    @Setter
    @Schema(description = "指定仓库下的 SKU 库存信息")
    public static class LocationStock {

        @Schema(description = "location 与 SKU 的复合主键")
        private String locationSku;

        @Schema(description = "仓库编码")
        private String locationCode;

        @Schema(description = "SKU 编码")
        private String sku;

        @Schema(description = "SMC 编码")
        private String smc;

        @Schema(description = "币种")
        private CurrencyCode currencyCode;

        @Schema(description = "商品价格")
        private BigDecimal price;

        @Schema(description = "可用库存数量")
        private BigDecimal quantity;
    }
}