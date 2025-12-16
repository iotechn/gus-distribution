package com.dobbinsoft.gus.distribution.client.erp.model.jdy;

import lombok.Data;

/**
 * ERP访问令牌响应数据
 */
@Data
public class JdyErpAccessTokenData {
    /**
     * 访问令牌
     */
    private String accessToken;

    /**
     * 过期时间（毫秒）
     */
    private Long expires;
} 