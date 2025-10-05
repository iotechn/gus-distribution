package com.dobbinsoft.gus.distribution.data.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * MQ Redis 连接配置属性
 */
@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "mq.redis")
public class MqRedisProperties {
    // Getters and Setters
    private int database = 1;
    private String password;
    private String host = "localhost";
    private int port = 6379;
    private String clientType = "lettuce";
    private String clientName;

}
