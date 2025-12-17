package com.dobbinsoft.gus.distribution.service;

import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpServletRequest;

/**
 * ERP 业务处理服务
 *
 * <p>说明：
 * <ul>
 *     <li>控制器负责：租户上下文、参数解析、签名校验等「接入层」逻辑</li>
 *     <li>本服务负责：与具体业务相关的处理逻辑</li>
 * </ul>
 */
public interface ErpService {

    /**
     * 处理 JDY ERP 回调（包含：查找 ERP 提供商、签名校验、业务处理）
     *
     * @param request  HttpServletRequest
     * @param tenantId 租户 ID（路径参数）
     * @param body     原始请求体
     * @return 返回给回调方的响应内容（带 HTTP 状态）
     */
    ResponseEntity<String> handleJdyCallback(HttpServletRequest request, String tenantId, String body);

}


