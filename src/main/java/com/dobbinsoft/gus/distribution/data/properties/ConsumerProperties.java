package com.dobbinsoft.gus.distribution.data.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Redis Stream 消费者配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "gus.distribution.consumer")
public class ConsumerProperties {

    /**
     * 默认最大重试次数
     */
    private int maxRetryCount = 3;

    /**
     * 默认每次处理的最大pending消息数
     */
    private int pendingBatchSize = 100;

    /**
     * 默认每次读取的新消息数量
     */
    private int newMessageCount = 10;

    /**
     * 默认阻塞超时时间（秒）
     */
    private int blockTimeoutSeconds = 2;

} 