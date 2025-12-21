package com.dobbinsoft.gus.distribution.client.gus.product;

import com.dobbinsoft.gus.distribution.client.erp.model.ErpUnitGroup;
import com.dobbinsoft.gus.web.vo.R;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "gus-product-unit", url = "${gus.distribution.product-url}", path = "/api/unit")
public interface ProductUnitFeignClient {

    @PostMapping("/sync")
    @Operation(summary = "Sync units from ERP", description = "Synchronize unit groups from ERP system")
    R<Void> syncUnits(@Valid @RequestBody List<ErpUnitGroup> unitGroups);

}

