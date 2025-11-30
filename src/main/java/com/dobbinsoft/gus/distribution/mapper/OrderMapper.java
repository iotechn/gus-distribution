package com.dobbinsoft.gus.distribution.mapper;

import com.dobbinsoft.gus.distribution.data.IMapper;
import com.dobbinsoft.gus.distribution.data.po.OrderPO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends IMapper<OrderPO> {

}
