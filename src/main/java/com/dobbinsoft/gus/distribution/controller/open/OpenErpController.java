package com.dobbinsoft.gus.distribution.controller.open;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dobbinsoft.gus.common.model.constant.HeaderConstants;
import com.dobbinsoft.gus.common.utils.context.GenericRequestContextHolder;
import com.dobbinsoft.gus.common.utils.context.bo.IdentityContext;
import com.dobbinsoft.gus.common.utils.context.bo.LanguageContext;
import com.dobbinsoft.gus.common.utils.context.bo.RequestProperty;
import com.dobbinsoft.gus.common.utils.context.bo.TenantContext;
import com.dobbinsoft.gus.common.utils.context.bo.TraceContext;
import com.dobbinsoft.gus.distribution.service.ErpService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

/**
 * ERP 回调接口入口
 *
 * <p>职责划分：
 * <ul>
 *     <li>负责：租户上下文初始化</li>
 *     <li>不负责：签名校验与具体业务处理，这部分全部下沉到 {@link ErpService}</li>
 * </ul>
 */
@RestController
@RequestMapping("/open/erp")
@RequiredArgsConstructor
public class OpenErpController {

    private final ErpService erpService;

    @PostMapping("/jdy/{tenantId}")
    public ResponseEntity<String> handleJdyErpCallback(HttpServletRequest request,
                                                       @PathVariable String tenantId,
                                                       @RequestBody(required = false) String body) {
        // 手动设置租户信息（开放接口不经过网关，需要自行写入租户上下文）
        RequestProperty requestProperty = RequestProperty.builder()
                .tenantContext(new TenantContext())
                .traceContext(new TraceContext())
                .identityContext(new IdentityContext())
                .languageContext(new LanguageContext())
                .build();
        requestProperty.setProperty(HeaderConstants.TENANT_ID.getValue(), tenantId);
        requestProperty.initContext();
        GenericRequestContextHolder.setRequestProperty(requestProperty);

        // 校验签名并执行业务逻辑（全部下沉到 ErpService）
        return erpService.handleJdyCallback(request, tenantId, body == null ? "" : body);
    }
}


