package com.dobbinsoft.gus.distribution.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dobbinsoft.gus.distribution.data.po.CartPO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CartMapper extends BaseMapper<CartPO> {
} 