package com.dobbinsoft.gus.distribution.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dobbinsoft.gus.distribution.data.po.CartItemPO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CartItemMapper extends BaseMapper<CartItemPO> {
} 