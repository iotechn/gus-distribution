package com.dobbinsoft.gus.distribution.data.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "gus.distribution.erp")
public class ErpProperties {

    private String jdyApiUrl;

}
