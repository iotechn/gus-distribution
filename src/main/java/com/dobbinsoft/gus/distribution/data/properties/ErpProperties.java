package com.dobbinsoft.gus.distribution.data.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "gus.distribution.erp")
public class ErpProperties {

    private String jdyApiUrl;

}
