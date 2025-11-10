package com.dobbinsoft.gus.distribution.client.gus.product;

import com.dobbinsoft.gus.distribution.client.gus.product.model.CategoryVO;
import com.dobbinsoft.gus.web.vo.R;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "gus-product-category", url = "${gus.distribution.product-url}", path = "/api/category")
public interface ProductCategoryFeignClient {

    @GetMapping("/tree")
    @Operation(summary = "Get Category Tree", description = "Get complete category tree structure")
    R<List<CategoryVO>> getCategoryTree();

}
