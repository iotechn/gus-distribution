package com.dobbinsoft.gus.distribution.client.gus.product;

import com.dobbinsoft.gus.common.model.vo.PageResult;
import com.dobbinsoft.gus.distribution.client.gus.product.model.BatchStockAdjustDTO;
import com.dobbinsoft.gus.distribution.client.gus.product.model.ItemStockVO;
import com.dobbinsoft.gus.distribution.client.gus.product.model.ListStockVO;
import com.dobbinsoft.gus.distribution.client.gus.product.model.StockSearchDTO;
import com.dobbinsoft.gus.web.vo.R;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "gus-product-stock", url = "${gus.distribution.product-url}", path = "/api/stock")
public interface ProductStockFeignClient {

    @GetMapping("/item/{smc}")
    R<ItemStockVO> itemStock(@PathVariable String smc);

    @PostMapping("/search")
    R<PageResult<ListStockVO>> search(
            @RequestBody @Valid StockSearchDTO stockSearchDTO);

    @PostMapping("/adjust")
    R<Void> adjustStock(@Valid @RequestBody BatchStockAdjustDTO batchStockAdjustDTO);

}
