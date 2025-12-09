package com.dobbinsoft.gus.distribution.mq.consumer;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.PendingMessage;
import org.springframework.data.redis.connection.stream.PendingMessages;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.connection.stream.StreamReadOptions;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.dobbinsoft.gus.common.utils.json.JsonUtil;
import com.dobbinsoft.gus.distribution.client.gus.permission.PermissionDlqFeignClient;
import com.dobbinsoft.gus.distribution.client.gus.permission.model.DlqCreateDTO;
import com.dobbinsoft.gus.distribution.client.gus.permission.model.DlqUpdateStatusDTO;
import com.fasterxml.jackson.core.type.TypeReference;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
/**
 * Redis Stream 消息消费抽象基类
 * 提供通用的消息调度、重试、死信队列等逻辑
 */
@Slf4j
public abstract class AbstractConsumer implements InitializingBean {

    @Autowired
    @Qualifier("mqStringRedisTemplate")
    protected StringRedisTemplate stringRedisTemplate;

    @Autowired
    protected PermissionDlqFeignClient permissionDlqFeignClient;

    // 标记系统是否正在关闭，用于在退出阶段降低日志级别，避免误报错误
    private volatile boolean shuttingDown = false;

    /**
     * 消费者配置类
     */
    @Data
    @Builder
    public static class ConsumerConfigBuilder {
        /**
         * Stream Key
         */
        private String streamKey;
        
        /**
         * 消费组名称
         */
        private String groupName;
        
        /**
         * 消费者名称前缀
         */
        private String consumerNamePrefix;
        
        /**
         * 最大重试次数
         */
        private int maxRetryCount;
        
        /**
         * 每次处理的最大pending消息数
         */
        private int pendingBatchSize;
        
        /**
         * 每次读取的新消息数量
         */
        private int newMessageCount;
        
        /**
         * 阻塞超时时间（秒）
         */
        private int blockTimeoutSeconds;

        /**
         * 参数校验
         */
        public void validate() {
            if (StringUtils.isEmpty(streamKey)) {
                throw new IllegalArgumentException("streamKey cannot be empty");
            }
            if (StringUtils.isEmpty(groupName)) {
                throw new IllegalArgumentException("groupName cannot be empty");
            }
            if (maxRetryCount < 0) {
                throw new IllegalArgumentException("maxRetryCount must be >= 0");
            }
            if (pendingBatchSize <= 0) {
                throw new IllegalArgumentException("pendingBatchSize must be > 0");
            }
            if (newMessageCount <= 0) {
                throw new IllegalArgumentException("newMessageCount must be > 0");
            }
            if (blockTimeoutSeconds <= 0) {
                throw new IllegalArgumentException("blockTimeoutSeconds must be > 0");
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        startConsumer();
    }

    /**
     * 获取消费者配置
     * 子类必须实现此方法并提供配置对象
     */
    protected abstract ConsumerConfigBuilder getConsumerConfig();

    /**
     * 处理单条消息的抽象方法
     * @param ops Stream操作对象
     * @param message 消息记录
     */
    protected abstract void processMessage(StreamOperations<String, Object, Object> ops, MapRecord<String, Object, Object> message);

    /**
     * 获取消息类型
     * 子类可以重写此方法提供具体的消息类型
     */
    protected String getMessageType() {
        return this.getClass().getSimpleName();
    }

    /**
     * 将消息移动到死信队列
     * @param messageId 消息ID
     * @param message 消息内容
     */
    protected void moveToDlq(String messageId, MapRecord<String, Object, Object> message) {
        try {
            ConsumerConfigBuilder config = getConsumerConfig();
            String messageValue = JsonUtil.convertToString(message.getValue());
            
            // 创建死信队列记录
            DlqCreateDTO dlqCreateDTO = new DlqCreateDTO();
            dlqCreateDTO.setMessageType(getMessageType());
            dlqCreateDTO.setStreamKey(config.getStreamKey());
            dlqCreateDTO.setMessageId(messageId);
            dlqCreateDTO.setMessageValue(messageValue);
            dlqCreateDTO.setErrorMessage("Message exceeded max retries");
            permissionDlqFeignClient.create(dlqCreateDTO);
            
            log.warn("{} Message moved to DLQ: {}", getLogPrefix(), messageId);
        } catch (Exception e) {
            log.error("{} Failed to create DLQ record for message: {}", getLogPrefix(), messageId, e);
        }
    }

    /**
     * 获取日志前缀
     */
    protected String getLogPrefix() {
        return "[" + this.getClass().getSimpleName() + "]";
    }

    /**
     * 启动消费者
     */
    public void startConsumer() {
        ConsumerConfigBuilder config = getConsumerConfig();
        config.validate();
        
        // 注册 JVM 关闭钩子，用于在优雅停机时降低日志级别
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            shuttingDown = true;
            try {
                log.info("{} Shutdown signal received. Stopping consumer loop...", getLogPrefix());
            } catch (Throwable ignored) {
                // ignore logging issues during shutdown
            }
        }, "Redis-Consumer-ShutdownHook-" + getLogPrefix()));
        
        StreamOperations<String, Object, Object> ops = stringRedisTemplate.opsForStream();
        String consumerName = config.getConsumerNamePrefix() + "_" + UUID.randomUUID().toString().replace("-", "");
        
        // 创建消费组（幂等）
        try {
            ops.createGroup(config.getStreamKey(), config.getGroupName());
            log.info("{} Created consumer group: {}", getLogPrefix(), config.getGroupName());
        } catch (Exception ignored) {
            log.info("{} Consumer group already exists: {}", getLogPrefix(), config.getGroupName());
        }

        // 启动消费线程
        Thread thread = new Thread(() -> {
            // TODO 这里的map无法解决集群问题
            // TODO ACK并不会让数据删除，后面改为
            //使用redis 7 生产端限长（最简单，代价低）
            //XADD MAXLEN ~ N：只保留最近 N 条，近似裁剪，性能好。
            //或 Redis 7 起支持按时间：XTRIM MINID ~ <ms-unix-time> 保留最近一段时间的数据。
            //Spring Data Redis 对应：在 add 时带上 XAddOptions maxlen/approximateTrimming；或后续调用 ops.trim(key, maxLen, true)。
            Map<String, Integer> retryCountMap = new ConcurrentHashMap<>();
            final int MAX_RETRY = config.getMaxRetryCount();
            final int PENDING_BATCH_SIZE = config.getPendingBatchSize();

            while (!shuttingDown) {
                try {
                    // 1. 处理PENDING消息
                    processPendingMessages(ops, consumerName, retryCountMap, MAX_RETRY, PENDING_BATCH_SIZE, config);

                    // 2. 处理新消息
                    processNewMessages(ops, consumerName, config);

                } catch (Exception e) {
                    if (shuttingDown || Thread.currentThread().isInterrupted() || isLettuceConnectionClosed(e)) {
                        log.warn("{} Main loop error during shutdown", getLogPrefix(), e);
                        break;
                    } else {
                        log.error("{} Main loop error", getLogPrefix(), e);
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }, "Redis-Consumer-" + getLogPrefix());
        thread.setDaemon(true);
        thread.start();
        log.info("{} Started consumer: {}", getLogPrefix(), consumerName);
    }

    /**
     * 处理PENDING消息
     */
    private void processPendingMessages(StreamOperations<String, Object, Object> ops, String consumerName,
                                      Map<String, Integer> retryCountMap, int maxRetry, int pendingBatchSize,
                                      ConsumerConfigBuilder config) {
        PendingMessages pendingMessages = ops.pending(
                config.getStreamKey(),
                config.getGroupName(),
                Range.unbounded(),
                pendingBatchSize
        );

        if (!pendingMessages.isEmpty()) {
            log.info("{} Found {} PENDING messages", getLogPrefix(), pendingMessages.size());

            for (PendingMessage pending : pendingMessages) {
                String msgId = pending.getIdAsString();
                int retryCount = retryCountMap.getOrDefault(msgId, 0) + 1;

                if (retryCount > maxRetry) {
                    log.error("{} Message exceeded max retries ({}). Ack and discard: {}",
                            getLogPrefix(), maxRetry, msgId);
                    ops.acknowledge(config.getStreamKey(), config.getGroupName(), msgId);
                    retryCountMap.remove(msgId);

                    // 尝试获取消息内容并移动到死信队列
                    try {
                        List<MapRecord<String, Object, Object>> claimed = ops.claim(
                                config.getStreamKey(),
                                config.getGroupName(),
                                consumerName,
                                Duration.ZERO,
                                RecordId.of(msgId)
                        );
                        if (!claimed.isEmpty()) {
                            // if not dlq record retry
                            Map<Object, Object> valueMap = claimed.getFirst().getValue();
                            String dlqRecordId = (String) valueMap.get("dlqRecordId");
                            if (dlqRecordId == null) {
                                moveToDlq(msgId, claimed.getFirst());
                            }
                        }
                    } catch (Exception e) {
                        log.error("{} Failed to move message to DLQ: {}", getLogPrefix(), msgId, e);
                    }
                    continue;
                }

                try {
                    // 使用XCLAIM获取消息内容
                    List<MapRecord<String, Object, Object>> claimed = ops.claim(
                            config.getStreamKey(),
                            config.getGroupName(),
                            consumerName,
                            Duration.ZERO,
                            RecordId.of(msgId)
                    );

                    if (!claimed.isEmpty()) {
                        MapRecord<String, Object, Object> message = claimed.get(0);
                        Map<String, String> previousMdc = setMdcFromMessage(message);
                        try {
                            processMessage(ops, message);
                            retryCountMap.remove(msgId); // 成功处理后清除重试计数
                        } finally {
                            restoreMdc(previousMdc);
                        }
                    }
                } catch (Exception e) {
                    retryCountMap.put(msgId, retryCount);
                    log.error("{} Failed pending message (retry #{}/{}): {}",
                            getLogPrefix(), retryCount, maxRetry, msgId, e);
                }
            }
        }
    }

    /**
     * 处理新消息
     */
    private void processNewMessages(StreamOperations<String, Object, Object> ops, String consumerName,
                                  ConsumerConfigBuilder config) {
        List<MapRecord<String, Object, Object>> newMessages = ops.read(
                Consumer.from(config.getGroupName(), consumerName),
                StreamReadOptions.empty()
                        .count(config.getNewMessageCount())
                        .block(Duration.ofSeconds(config.getBlockTimeoutSeconds())),
                StreamOffset.create(config.getStreamKey(), ReadOffset.lastConsumed())
        );

        if (newMessages != null && !newMessages.isEmpty()) {
            log.info("{} Processing {} NEW messages", getLogPrefix(), newMessages.size());
            for (MapRecord<String, Object, Object> message : newMessages) {
                Map<String, String> previousMdc = setMdcFromMessage(message);
                try {
                    processMessage(ops, message);
                } catch (Exception e) {
                    log.error("{} Failed new message: {}", getLogPrefix(), message.getId(), e);
                } finally {
                    restoreMdc(previousMdc);
                }
            }
        }
    }

    /**
     * 确认消息
     */
    protected void acknowledgeMessage(StreamOperations<String, Object, Object> ops, MapRecord<String, Object, Object> message) {
        ConsumerConfigBuilder config = getConsumerConfig();
        ops.acknowledge(config.getStreamKey(), config.getGroupName(), message.getId());
        log.info("{} Message acknowledged: {}", getLogPrefix(), message.getId());

        Map<Object, Object> valueMap = message.getValue();
        String dlqRecordId = (String) valueMap.get("dlqRecordId");
        // 如果有死信队列记录ID，更新状态为成功
        if (dlqRecordId != null) {
            try {
                DlqUpdateStatusDTO dlqUpdateStatusDTO = new DlqUpdateStatusDTO();
                dlqUpdateStatusDTO.setDlqRecordId(dlqRecordId);
                dlqUpdateStatusDTO.setStatus(DlqUpdateStatusDTO.Status.SUCCESS);
                permissionDlqFeignClient.updateStatus(dlqUpdateStatusDTO);
                log.info("{} Updated DLQ record status to SUCCESS: {}", getLogPrefix(), dlqRecordId);
            } catch (Exception e) {
                log.error("{} Failed to update DLQ record status: {}", getLogPrefix(), dlqRecordId, e);
            }
        }
    }

    // MDC helpers: set from message and restore previous
    private Map<String, String> setMdcFromMessage(MapRecord<String, Object, Object> message) {
        Map<String, String> previous = MDC.getCopyOfContextMap();
        try {
            Map<Object, Object> value = message.getValue();
            Object mdcObj = value.get("mdc");
            if (mdcObj == null) {
                return previous;
            }
            Map<String, String> mdcMap = null;
            if (mdcObj instanceof String mdcJson) {
                try {
                    mdcMap = JsonUtil.convertValue(mdcJson, new TypeReference<Map<String, String>>() {});
                } catch (Exception ignore) {
                    // ignore parsing issues; leave MDC unchanged
                }
            } else if (mdcObj instanceof Map<?, ?> rawMap) {
                Map<String, String> tmp = new HashMap<>();
                for (Map.Entry<?, ?> e : rawMap.entrySet()) {
                    tmp.put(String.valueOf(e.getKey()), e.getValue() == null ? null : String.valueOf(e.getValue()));
                }
                mdcMap = tmp;
            }

            MDC.clear();
            if (mdcMap != null) {
                for (Map.Entry<String, String> e : mdcMap.entrySet()) {
                    if (e.getKey() != null && e.getValue() != null) {
                        MDC.put(e.getKey(), e.getValue());
                    }
                }
            }
        } catch (Exception e) {
            log.warn("{} Failed to set MDC from message: {}", getLogPrefix(), message.getId(), e);
        }
        return previous;
    }

    private void restoreMdc(Map<String, String> previous) {
        MDC.clear();
        if (previous != null) {
            for (Map.Entry<String, String> e : previous.entrySet()) {
                if (e.getKey() != null && e.getValue() != null) {
                    MDC.put(e.getKey(), e.getValue());
                }
            }
        }
    }

    // 判断是否为 Lettuce 在连接关闭时抛出的异常，用于优雅停机时降级日志
    private boolean isLettuceConnectionClosed(Throwable t) {
        while (t != null) {
            if (t instanceof io.lettuce.core.RedisException) {
                String msg = t.getMessage();
                if (msg != null && msg.contains("Connection closed")) {
                    return true;
                }
            }
            t = t.getCause();
        }
        return false;
    }
} 
