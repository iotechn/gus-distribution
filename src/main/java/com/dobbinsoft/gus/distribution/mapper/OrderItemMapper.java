package com.dobbinsoft.gus.distribution.mapper;

import com.dobbinsoft.gus.distribution.data.po.OrderItemPO;
import com.dobbinsoft.gus.distribution.mapper.mybatis.MybatisOrderItemMapper;
import org.springframework.stereotype.Component;

@Component
public class OrderItemMapper extends MapperAdapter<MybatisOrderItemMapper, OrderItemPO> {
}

