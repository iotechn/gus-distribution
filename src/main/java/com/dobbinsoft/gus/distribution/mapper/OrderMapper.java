package com.dobbinsoft.gus.distribution.mapper;

import com.dobbinsoft.gus.distribution.data.po.OrderPO;
import com.dobbinsoft.gus.distribution.mapper.mybatis.MybatisOrderMapper;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper extends MapperAdapter<MybatisOrderMapper, OrderPO> {
}

