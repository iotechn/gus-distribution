package com.dobbinsoft.gus.distribution.config.mybatis;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import org.apache.ibatis.mapping.SqlCommandType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class MybatisInterceptorConfig {

    @Bean
    public EcommerceTenantLineHandler distributionTenantLineHandler() {
        return new EcommerceTenantLineHandler();
    }

    @Bean
    public TenantLineInnerInterceptor tenantLineInnerInterceptor(EcommerceTenantLineHandler distributionTenantLineHandler) {
        TenantLineInnerInterceptor tenantLineInnerInterceptor = new TenantLineInnerInterceptor();
        tenantLineInnerInterceptor.setTenantLineHandler(distributionTenantLineHandler);
        return tenantLineInnerInterceptor;
    }

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(TenantLineInnerInterceptor tenantLineInnerInterceptor) {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
//        interceptor.addInnerInterceptor(dataChangeRecorderInnerInterceptor());
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        interceptor.addInnerInterceptor(tenantLineInnerInterceptor);
        return interceptor;
    }

}
