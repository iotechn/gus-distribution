package com.dobbinsoft.gus.distribution.client.gus.product;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.dobbinsoft.gus.common.model.vo.PageResult;
import com.dobbinsoft.gus.distribution.client.erp.model.ErpItem;
import com.dobbinsoft.gus.distribution.client.gus.product.model.ItemDetailVO;
import com.dobbinsoft.gus.distribution.client.gus.product.model.ItemIncreaseSalesDTO;
import com.dobbinsoft.gus.distribution.client.gus.product.model.ItemSearchDTO;
import com.dobbinsoft.gus.distribution.client.gus.product.model.ItemVO;
import com.dobbinsoft.gus.web.vo.R;

import jakarta.validation.Valid;

@FeignClient(name = "gus-product-item", url = "${gus.distribution.product-url}", path = "/api/item")
public interface ProductItemFeignClient {

    @PostMapping("/search")
    R<PageResult<ItemVO>> search(@Valid @RequestBody ItemSearchDTO searchDTO);

    @GetMapping("/smc/{smc}")
    R<ItemDetailVO> getBySmc(@PathVariable String smc);

    @GetMapping("/sku/{sku}")
    R<ItemDetailVO> getBySku(@PathVariable String sku);

    @PutMapping("/smc/{smc}/sales/increase")
    R<Void> increaseSalesVolume(@PathVariable String smc, @Valid @RequestBody ItemIncreaseSalesDTO increaseSalesDTO);

    @PostMapping("/sync")
    R<Void> syncItems(@Valid @RequestBody List<ErpItem> items);

}
