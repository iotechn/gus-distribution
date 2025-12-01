package com.dobbinsoft.gus.distribution.mapper.mybatis;

import com.dobbinsoft.gus.distribution.data.IMapper;
import com.dobbinsoft.gus.distribution.data.po.OrderPO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MybatisOrderMapper extends IMapper<OrderPO> {

}
