package com.dobbinsoft.gus.distribution.config;

import com.dobbinsoft.gus.distribution.data.properties.MqRedisProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * MQ Redis 配置类
 * 为消息队列功能提供独立的Redis实例配置
 */
@Configuration
public class MqRedisConfig {
    /**
     * MQ Redis 连接工厂
     */
    @Bean("mqRedisConnectionFactory")
    public RedisConnectionFactory mqRedisConnectionFactory(MqRedisProperties mqRedisProperties) {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(mqRedisProperties.getHost());
        config.setPort(mqRedisProperties.getPort());
        config.setDatabase(mqRedisProperties.getDatabase());
        
        if (mqRedisProperties.getPassword() != null && !mqRedisProperties.getPassword().isEmpty()) {
            config.setPassword(mqRedisProperties.getPassword());
        }

        // 注意：setClientName 方法已废弃，可以通过 LettuceClientConfiguration 设置
        return new LettuceConnectionFactory(config);
    }

    /**
     * MQ Redis StringRedisTemplate
     * 专门用于消息队列操作
     */
    @Bean("mqStringRedisTemplate")
    public StringRedisTemplate mqStringRedisTemplate(@Qualifier("mqRedisConnectionFactory") RedisConnectionFactory mqRedisConnectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(mqRedisConnectionFactory);
        return template;
    }
}
