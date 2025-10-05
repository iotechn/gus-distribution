package com.dobbinsoft.gus.distribution.config.mybatis;


import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.dobbinsoft.gus.common.utils.context.GenericRequestContextHolder;
import com.dobbinsoft.gus.common.utils.context.bo.TenantContext;
import com.dobbinsoft.gus.web.exception.BasicErrorCode;
import com.dobbinsoft.gus.web.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

@Slf4j
public class EcommerceTenantLineHandler implements TenantLineHandler {

    @Override
    public Expression getTenantId() {
        Optional<TenantContext> tenantContextOptional = GenericRequestContextHolder.getTenantContext();
        if (tenantContextOptional.isEmpty() || StringUtils.isEmpty(tenantContextOptional.get().getTenantId())) {
            log.error("tenant context is empty");
            throw new ServiceException(BasicErrorCode.NO_PERMISSION);
        }
        return new StringValue(tenantContextOptional.get().getTenantId());
    }
}
