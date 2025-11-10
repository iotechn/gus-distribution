package com.dobbinsoft.gus.distribution.data.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "gus.distribution")
public class DistributionProperties {

    /**
     * Payment endpoint
     */
    private String paymentUrl;

    /**
     * logistics endpoint
     */
    private String logisticsUrl;

    /**
     * location endpoint
     */
    private String locationUrl;

    /**
     * product endpoint
     */
    private String productUrl;

    /**
     * 用于对数据库敏感字段进行加密/解密
     */
    private String aesKey;

    /**
     * token 过期时间
     */
    private Integer expiresIn;

    /**
     * refresh token过期时间，默认30天
     */
    private Integer refreshExpiresIn = 30 * 24 * 60 * 60;

    /**
     * token 签发机构
     */
    private String iss;
    /**
     * private key for sign jwt token.
     */
    private String privateKey;


}
