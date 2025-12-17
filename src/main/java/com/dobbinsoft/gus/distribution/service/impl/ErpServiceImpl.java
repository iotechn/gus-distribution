package com.dobbinsoft.gus.distribution.service.impl;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dobbinsoft.gus.distribution.client.erp.ErpClient;
import com.dobbinsoft.gus.distribution.client.erp.model.ErpEvent;
import com.dobbinsoft.gus.distribution.data.po.ErpProviderPO;
import com.dobbinsoft.gus.distribution.mapper.ErpProviderMapper;
import com.dobbinsoft.gus.distribution.service.ErpService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * ERP 业务处理实现
 *
 * <p>当前只支持 JDY，后续如果扩展其它 ERP，可在此根据 {@link ErpProviderPO#getType()} 做分发。</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ErpServiceImpl implements ErpService {

    private final ErpProviderMapper erpProviderMapper;
    private final ErpClient erpClient;

    @Override
    public ResponseEntity<String> handleJdyCallback(HttpServletRequest request, String tenantId, String body) {
        // 1. 查找当前租户唯一的 ERP 提供商（约定：同一租户只会有一个 ErpProvider）
        ErpProviderPO erpProviderPO = erpProviderMapper.selectOne(
                new QueryWrapper<ErpProviderPO>().eq("tenant_id", tenantId)
        );
        if (erpProviderPO == null) {
            log.warn("[ERP] 回调接收失败，tenantId={} 未配置 ERP 提供商", tenantId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("ERP provider not configured");
        }

        // 2. 调用 ERP 客户端做签名校验
        String originalConfig = erpProviderPO.getConfig();
        boolean valid = erpClient.validateCallback(request, body == null ? "" : body, erpProviderPO);
        if (!valid) {
            log.warn("[ERP] 回调验签失败，tenantId={}，providerId={}", tenantId, erpProviderPO.getId());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid signature");
        }

        if (!originalConfig.equals(erpProviderPO.getConfig())) {
            // 配置被更新，需要持久化
            erpProviderMapper.updateById(erpProviderPO);
            log.info("[ERP] 配置被更新，tenantId={}，providerId={}", tenantId, erpProviderPO.getId());
            // 更新配置的请求，不需要处理业务逻辑，直接返回成功
            return ResponseEntity.ok("success");
        }

        // 3. 业务处理
        List<ErpEvent> events = erpClient.convertToEvents(request, body);
        for (ErpEvent event : events) {
            switch (event.getType()) {
                case ErpEvent.Type.INVENTORY_CHANGED:
                    // TODO handleInventoryChanged(event);
                    break;
                case ErpEvent.Type.SALE_OUTBOUND_CREATED:
                    // TODO handleSaleOutboundCreated(event);
                    break;
            }
        }
        return ResponseEntity.ok("success");
    }
}


