package com.dobbinsoft.gus.distribution.mapper;

import com.dobbinsoft.gus.distribution.data.po.CartPO;
import com.dobbinsoft.gus.distribution.mapper.mybatis.MybatisCartMapper;
import org.springframework.stereotype.Component;

@Component
public class CartMapper extends MapperAdapter<MybatisCartMapper, CartPO> {
}

