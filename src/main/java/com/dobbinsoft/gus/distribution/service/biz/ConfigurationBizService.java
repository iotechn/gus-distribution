package com.dobbinsoft.gus.distribution.service.biz;

import com.dobbinsoft.gus.common.utils.context.GenericRequestContextHolder;
import com.dobbinsoft.gus.common.utils.context.bo.TenantContext;
import com.dobbinsoft.gus.common.utils.json.JsonUtil;
import com.dobbinsoft.gus.distribution.data.dto.configuration.ConfigurationDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class ConfigurationBizService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final String CONFIGURATION_KEY = "gus:configuration:";

    public ConfigurationDTO getConfiguration() {
        TenantContext tenantContext = GenericRequestContextHolder.getTenantContext().orElseThrow();
        String json = stringRedisTemplate.opsForValue().get(CONFIGURATION_KEY.concat(tenantContext.getTenantId()));
        if (StringUtils.isEmpty(json)) {
            return ConfigurationDTO.defaultConfiguration();
        }
        return JsonUtil.convertValue(json, ConfigurationDTO.class);
    }

}
