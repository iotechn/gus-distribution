package com.dobbinsoft.gus.distribution.mapper.mybatis;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dobbinsoft.gus.distribution.data.po.AddressPO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MybatisAddressMapper extends BaseMapper<AddressPO> {
} 