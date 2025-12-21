package com.dobbinsoft.gus.distribution.client.gus.product;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.dobbinsoft.gus.distribution.client.erp.model.ErpCategory;
import com.dobbinsoft.gus.distribution.client.gus.product.model.CategoryVO;
import com.dobbinsoft.gus.web.vo.R;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

@FeignClient(name = "gus-product-category", url = "${gus.distribution.product-url}", path = "/api/category")
public interface ProductCategoryFeignClient {

    @GetMapping("/tree")
    @Operation(summary = "Get Category Tree", description = "Get complete category tree structure")
    R<List<CategoryVO>> getCategoryTree();

    @PostMapping("/sync")
    @Operation(summary = "Sync categories from ERP", description = "Synchronize categories from ERP system")
    R<Void> syncCategories(@Valid @RequestBody List<ErpCategory> categories);

}
