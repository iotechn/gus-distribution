package com.dobbinsoft.gus.distribution.client.erp.model.jdy;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JdyErpConfigModel {
    
    /**
     * key 回调中设置，backoffice不可编辑
     */
    private String key;

    /**
     * secret 回调中设置，backoffice不可编辑
     */
    private String secret;

    /**
     * Client Id， backoffice中设置
     */
    private String clientId;

    /**
     * Client Secret， backoffice中设置
     */
    private String clientSecret;

    /**
     * 帐套ID， backoffice中设置
     */
    private String accountId;

    /**
     * ERP客户编码：下单时如果用户没有设置客户编码，则默认使用这个
     */
    private String customerCode;
}
