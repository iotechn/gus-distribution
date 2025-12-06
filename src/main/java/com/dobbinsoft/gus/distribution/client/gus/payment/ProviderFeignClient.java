package com.dobbinsoft.gus.distribution.client.gus.payment;

import com.dobbinsoft.gus.distribution.client.gus.payment.model.OpenProviderVO;
import com.dobbinsoft.gus.distribution.config.OpenFeignConfig;
import com.dobbinsoft.gus.web.vo.R;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(
        name = "payment-provider",
        url = "${gus.distribution.payment-url}",
        path = "/api/provider",
        configuration = OpenFeignConfig.class)
public interface ProviderFeignClient {

    @Operation(summary = "查询租户下所有支付供应商（不包含配置）", description = "返回当前租户下的所有支付供应商基础信息，脱敏不包含config，供下游系统使用")
    @GetMapping
    R<List<OpenProviderVO>> listProviders();

}

