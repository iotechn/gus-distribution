package com.dobbinsoft.gus.distribution.config.mybatis;

import com.dobbinsoft.gus.distribution.data.handler.AddressTypeHandler;
import com.dobbinsoft.gus.distribution.data.po.OrderPO;
import jakarta.annotation.PostConstruct;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisPlusConfig {

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Autowired
    private AddressTypeHandler addressTypeHandler;

    @PostConstruct
    public void registerTypeHandlers() {
        TypeHandlerRegistry typeHandlerRegistry = sqlSessionFactory.getConfiguration().getTypeHandlerRegistry();
        typeHandlerRegistry.register(OrderPO.Address.class, addressTypeHandler);
    }


} 