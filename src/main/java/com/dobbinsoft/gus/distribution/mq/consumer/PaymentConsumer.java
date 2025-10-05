package com.dobbinsoft.gus.distribution.mq.consumer;

import com.dobbinsoft.gus.common.model.constant.HeaderConstants;
import com.dobbinsoft.gus.common.utils.context.GenericRequestContextHolder;
import com.dobbinsoft.gus.common.utils.context.bo.*;
import com.dobbinsoft.gus.common.utils.json.JsonUtil;
import com.dobbinsoft.gus.distribution.client.gus.payment.model.TransactionUpdateEventDTO;
import com.dobbinsoft.gus.distribution.data.constant.DistributionConstants;
import com.dobbinsoft.gus.distribution.data.properties.ConsumerProperties;
import com.dobbinsoft.gus.distribution.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PaymentConsumer extends AbstractConsumer {

    @Autowired
    private ConsumerProperties consumerProperties;

    @Autowired
    private OrderService orderService;

    @Override
    protected ConsumerConfigBuilder getConsumerConfig() {
        return ConsumerConfigBuilder.builder()
                .streamKey(DistributionConstants.PAYMENT_CALLBACK_STREAM_KEY)
                .groupName(DistributionConstants.GROUP)
                .consumerNamePrefix(DistributionConstants.GROUP)
                .maxRetryCount(consumerProperties.getMaxRetryCount())
                .pendingBatchSize(consumerProperties.getPendingBatchSize())
                .newMessageCount(consumerProperties.getNewMessageCount())
                .blockTimeoutSeconds(consumerProperties.getBlockTimeoutSeconds())
                .build();
    }

    @Override
    protected void processMessage(StreamOperations<String, Object, Object> ops, MapRecord<String, Object, Object> message) {
        // 数据
        Map<Object, Object> value = message.getValue();
        String json = value.get("eventDTO").toString();
        String tenantId = value.get("tenantId").toString();

        // 手动设置租户信息
        RequestProperty requestProperty = RequestProperty.builder()
                .tenantContext(new TenantContext())
                .traceContext(new TraceContext())
                .identityContext(new IdentityContext())
                .languageContext(new LanguageContext())
                .build();
        requestProperty.setProperty(HeaderConstants.TENANT_ID.name(), tenantId);
        requestProperty.initContext();
        GenericRequestContextHolder.setRequestProperty(requestProperty);

        // 处理业务
        TransactionUpdateEventDTO transactionUpdateEventDTO = JsonUtil.convertToObject(json, TransactionUpdateEventDTO.class);
        
        // 调用订单服务处理支付回调
        orderService.handlePaymentCallback(transactionUpdateEventDTO);
    }
}
