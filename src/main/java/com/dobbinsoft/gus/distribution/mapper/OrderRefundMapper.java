package com.dobbinsoft.gus.distribution.mapper;

import com.dobbinsoft.gus.distribution.data.po.OrderRefundPO;
import com.dobbinsoft.gus.distribution.mapper.mybatis.MybatisOrderRefundMapper;
import org.springframework.stereotype.Component;

@Component
public class OrderRefundMapper extends MapperAdapter<MybatisOrderRefundMapper, OrderRefundPO> {
}

