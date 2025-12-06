package com.dobbinsoft.gus.distribution.controller.fo;

import com.dobbinsoft.gus.distribution.client.gus.payment.ProviderFeignClient;
import com.dobbinsoft.gus.distribution.client.gus.payment.model.OpenProviderVO;
import com.dobbinsoft.gus.web.vo.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "支付", description = "用户端支付相关接口")
@RestController
@RequestMapping("/fo/payment")
public class PaymentController {

    @Autowired
    private ProviderFeignClient providerFeignClient;

    @Operation(summary = "查询租户下所有支付供应商（不包含配置）", description = "返回当前租户下的所有支付供应商基础信息，脱敏不包含config，供前端使用")
    @GetMapping("/providers")
    public R<List<OpenProviderVO>> listProviders() {
        return providerFeignClient.listProviders();
    }
}

