package com.dobbinsoft.gus.distribution.controller.bo;

import com.dobbinsoft.gus.distribution.client.configcenter.ConfigCenterClient;
import com.dobbinsoft.gus.distribution.client.configcenter.vo.ConfigContentVO;
import com.dobbinsoft.gus.web.exception.BasicErrorCode;
import com.dobbinsoft.gus.web.exception.ServiceException;
import com.dobbinsoft.gus.web.vo.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * BO配置中心控制器
 */
@Slf4j
@RestController
@RequestMapping("/bo/config")
@Tag(name = "BO配置中心", description = "Backoffice配置管理接口")
public class BoConfigCenterController {

    @Resource
    private ConfigCenterClient configCenterClient;
    
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping
    @Operation(summary = "获取当前租户配置", description = "获取当前租户的所有配置信息")
    public R<ConfigContentVO> getConfig() {
        ConfigContentVO config = configCenterClient.getBrandAllConfigContent();
        return R.success(config);
    }

    @PostMapping
    @Operation(summary = "保存配置", description = "保存当前租户的配置信息")
    public R<Void> saveConfig(@RequestBody ConfigContentVO configContentVO) {
        configCenterClient.save(configContentVO);
        return R.success();
    }

    @PostMapping("/reset")
    @Operation(summary = "重置配置", description = "重置当前租户的配置为默认值")
    public R<Void> resetConfig() {
        // 删除现有配置
        String tenantId = getTenantId();
        String cacheKey = "config:tenant:" + tenantId;
        stringRedisTemplate.delete(cacheKey);
        
        // 然后获取默认配置（会自动创建并保存）
        configCenterClient.getBrandAllConfigContent();
        return R.success();
    }
    
    private String getTenantId() {
        return com.dobbinsoft.gus.common.utils.context.GenericRequestContextHolder.getTenantContext()
                .map(com.dobbinsoft.gus.common.utils.context.bo.TenantContext::getTenantId)
                .orElseThrow(() -> new ServiceException(BasicErrorCode.SYSTEM_ERROR));
    }
}
