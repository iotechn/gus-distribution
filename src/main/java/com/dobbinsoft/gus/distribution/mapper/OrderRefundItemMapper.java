package com.dobbinsoft.gus.distribution.mapper;

import com.dobbinsoft.gus.distribution.data.po.OrderRefundItemPO;
import com.dobbinsoft.gus.distribution.mapper.mybatis.MybatisOrderRefundItemMapper;
import org.springframework.stereotype.Component;

@Component
public class OrderRefundItemMapper extends MapperAdapter<MybatisOrderRefundItemMapper, OrderRefundItemPO> {
}

