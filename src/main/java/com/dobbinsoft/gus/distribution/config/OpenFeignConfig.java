package com.dobbinsoft.gus.distribution.config;

import com.dobbinsoft.gus.common.model.constant.HeaderConstants;
import com.dobbinsoft.gus.common.utils.context.GenericRequestContextHolder;
import com.dobbinsoft.gus.common.utils.context.bo.RequestProperty;
import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.support.JsonFormWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Slf4j
@Configuration
public class OpenFeignConfig {


    @Value("${spring.application.name:gus-distribution}")
    private String applicationName;

    /**
     * Feign请求拦截器，自动添加调用链相关的Header
     * 包括traceId、语言、用户信息等
     */
    @Bean
    public RequestInterceptor feignRequestInterceptor() {
        return template -> {
            // 从当前请求上下文中获取请求属性
            Optional<RequestProperty> requestPropertyOpt = GenericRequestContextHolder.getRequestProperty();

            if (requestPropertyOpt.isPresent()) {
                RequestProperty requestProperty = requestPropertyOpt.get();

                // 遍历所有HeaderConstants，将当前请求的Header传递给下游服务
                for (HeaderConstants header : HeaderConstants.values()) {
                    String headerValue = requestProperty.getProperty(header.getValue());
                    if (headerValue != null && !headerValue.trim().isEmpty()) {
                        template.header(header.getValue(), headerValue);
                        log.debug("Feign请求添加Header: {} = {}", header.getValue(), headerValue);
                    }
                }
            } else {
                log.warn("Feign请求拦截器未找到请求上下文，无法传递Header");
            }

            template.header(HeaderConstants.APPLICATION_NAME.getValue(), applicationName);
        };
    }

    @Bean
    public JsonFormWriter jsonFormWriter() {
        return new JsonFormWriter();
    }

}
