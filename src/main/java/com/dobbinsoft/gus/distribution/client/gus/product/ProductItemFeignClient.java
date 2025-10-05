package com.dobbinsoft.gus.distribution.client.gus.product;

import com.dobbinsoft.gus.common.model.vo.PageResult;
import com.dobbinsoft.gus.distribution.client.gus.product.model.ItemSearchDTO;
import com.dobbinsoft.gus.distribution.client.gus.product.model.ItemVO;
import com.dobbinsoft.gus.web.vo.R;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "gus-product-item", url = "http://gus-product", path = "/api/item")
public interface ProductItemFeignClient {

    @PostMapping("/search")
    R<PageResult<ItemVO>> search(@Valid @RequestBody ItemSearchDTO searchDTO);

    @GetMapping("/smc/{smc}")
    R<ItemVO> getBySmc(@PathVariable String smc);

    @GetMapping("/sku/{sku}")
    R<ItemVO> getBySku(@PathVariable String sku);

}
