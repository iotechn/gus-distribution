package com.dobbinsoft.gus.distribution.client.erp.model.jdy;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JdyErpConfigModel {
    
    private String key;

    private String secret;

    private String clientSecret;

    private String clientId;

    private String accountId;

    private String provider;

    private String customerCode;
}
