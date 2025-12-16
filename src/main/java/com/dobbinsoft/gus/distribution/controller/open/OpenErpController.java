package com.dobbinsoft.gus.distribution.controller.open;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dobbinsoft.gus.common.model.constant.HeaderConstants;
import com.dobbinsoft.gus.common.utils.context.GenericRequestContextHolder;
import com.dobbinsoft.gus.common.utils.context.bo.IdentityContext;
import com.dobbinsoft.gus.common.utils.context.bo.LanguageContext;
import com.dobbinsoft.gus.common.utils.context.bo.RequestProperty;
import com.dobbinsoft.gus.common.utils.context.bo.TenantContext;
import com.dobbinsoft.gus.common.utils.context.bo.TraceContext;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.servlet.http.HttpServletRequest;

/**
 * ERP 回调接口入口
 */
@RestController
@RequestMapping("/open/erp")
public class OpenErpController {
    
    @RequestMapping("/jdy/{tenantId}")
    public ResponseEntity<String> handleJdyErpCallback(HttpServletRequest request, @PathVariable String tenantId, @RequestBody(required = false) String body) {
        // 手动设置租户信息
        RequestProperty requestProperty = RequestProperty.builder()
            .tenantContext(new TenantContext())
            .traceContext(new TraceContext())
            .identityContext(new IdentityContext())
            .languageContext(new LanguageContext())
            .build();
        requestProperty.setProperty(HeaderConstants.TENANT_ID.getValue(), tenantId);
        requestProperty.initContext();
        GenericRequestContextHolder.setRequestProperty(requestProperty);

        // 开始处理回调
        return ResponseEntity.ok("success");
    }
}
