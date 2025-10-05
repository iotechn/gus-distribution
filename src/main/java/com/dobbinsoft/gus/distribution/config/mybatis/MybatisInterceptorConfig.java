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

//   后续可用于session同步等操作
//    @Bean
//    public DataChangeRecorderInnerInterceptor dataChangeRecorderInnerInterceptor() {
//        return new DataChangeRecorderInnerInterceptor() {
//            @Override
//            protected List<SqlCommandType> focusOnMethods() {
//                return List.of(SqlCommandType.UPDATE, SqlCommandType.INSERT);
//            }
//
//            @Override
//            protected List<String> focusOnTables() {
//                return List.of("ec_file_item");
//            }
//
//            @Override
//            protected void dealOperationResult(OperationResult operationResult) {
//                System.out.println(operationResult);
//            }
//        };
//    }

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
