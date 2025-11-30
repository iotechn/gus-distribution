package com.dobbinsoft.gus.distribution.mapper;

import com.dobbinsoft.gus.distribution.data.IMapper;
import com.dobbinsoft.gus.distribution.data.po.OrderItemPO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderItemMapper extends IMapper<OrderItemPO> {
}
