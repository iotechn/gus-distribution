package com.dobbinsoft.gus.distribution.controller.fo;

import com.dobbinsoft.gus.common.model.vo.PageResult;
import com.dobbinsoft.gus.distribution.client.gus.product.model.ItemSearchDTO;
import com.dobbinsoft.gus.distribution.client.gus.product.model.ItemVO;
import com.dobbinsoft.gus.distribution.service.ItemService;
import com.dobbinsoft.gus.web.vo.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "商品管理", description = "用户端商品相关接口")
@RestController
@RequestMapping("/fo/item")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @Operation(summary = "搜索商品", description = "根据条件搜索商品列表")
    @PostMapping("/search")
    public R<PageResult<ItemVO>> search(@Valid @RequestBody ItemSearchDTO searchDTO) {
        PageResult<ItemVO> result = itemService.search(searchDTO);
        return R.success(result);
    }

    @Operation(summary = "根据SMC获取商品", description = "根据商品SMC获取商品详情")
    @GetMapping("/smc/{smc}")
    public R<ItemVO> getBySmc(
            @Parameter(description = "商品SMC", required = true)
            @PathVariable String smc) {
        ItemVO itemVO = itemService.getBySmc(smc);
        return R.success(itemVO);
    }

    @Operation(summary = "根据SKU获取商品", description = "根据商品SKU获取商品详情")
    @GetMapping("/sku/{sku}")
    public R<ItemVO> getBySku(
            @Parameter(description = "商品SKU", required = true)
            @PathVariable String sku) {
        ItemVO itemVO = itemService.getBySku(sku);
        return R.success(itemVO);
    }
}
