package com.dobbinsoft.gus.distribution.config;

import io.swagger.v3.oas.models.Paths;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Value("${spring.application.name}")
    private String appName;

    @Bean
    public GlobalOpenApiCustomizer pathPrefixCustomizer() {
        return openApi -> {
            // 创建新路径映射（添加前缀）
            Paths newPaths = new Paths();
            openApi.getPaths().forEach((path, pathItem) -> 
                newPaths.put("/" + appName + path, pathItem)
            );
            
            // 替换原始路径
            openApi.setPaths(newPaths);
        };
    }
}