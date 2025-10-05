package com.dobbinsoft.gus.distribution.config;

import com.dobbinsoft.gus.distribution.interceptor.BoInterceptor;
import com.dobbinsoft.gus.distribution.interceptor.FoInterceptor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Slf4j
public class EcInterceptorConfig implements WebMvcConfigurer {

    @Bean
    public FoInterceptor foInterceptor() {
        return new FoInterceptor();
    }

    @Bean
    public BoInterceptor boInterceptor() {
        return new BoInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(boInterceptor()).addPathPatterns("/bo/**");
        registry.addInterceptor(foInterceptor()).addPathPatterns("/fo/**");
    }

}
