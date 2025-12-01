package com.dobbinsoft.gus.distribution.mapper;

import com.dobbinsoft.gus.distribution.data.po.CartItemPO;
import com.dobbinsoft.gus.distribution.mapper.mybatis.MybatisCartItemMapper;
import org.springframework.stereotype.Component;

@Component
public class CartItemMapper extends MapperAdapter<MybatisCartItemMapper, CartItemPO> {
}

