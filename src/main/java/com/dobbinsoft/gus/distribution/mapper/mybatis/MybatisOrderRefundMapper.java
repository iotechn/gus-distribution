package com.dobbinsoft.gus.distribution.mapper.mybatis;

import com.dobbinsoft.gus.distribution.data.IMapper;
import com.dobbinsoft.gus.distribution.data.po.OrderRefundPO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MybatisOrderRefundMapper extends IMapper<OrderRefundPO> {
}
