package com.dobbinsoft.gus.distribution.client.gus.permission;

import com.dobbinsoft.gus.distribution.client.gus.permission.model.DlqCreateDTO;
import com.dobbinsoft.gus.distribution.client.gus.permission.model.DlqUpdateStatusDTO;
import com.dobbinsoft.gus.web.vo.R;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "gus-permission", url = "http://gus-permission", path = "/api/dlq")
public interface PermissionDlqFeignClient {

    @PostMapping("/create")
    @Operation(summary = "Create dead letter queue record")
    public R<Void> create(
            @RequestBody @Valid DlqCreateDTO createDTO);


    @PutMapping("/update-status")
    @Operation(summary = "Update dead letter queue record status")
    public R<Void> updateStatus(@RequestBody @Valid DlqUpdateStatusDTO updateStatusDTO);

}
