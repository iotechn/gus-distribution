package com.dobbinsoft.gus.distribution.mapper;

import com.dobbinsoft.gus.distribution.data.po.ErpProviderPO;
import com.dobbinsoft.gus.distribution.mapper.mybatis.MybatisErpProviderMapper;
import org.springframework.stereotype.Component;

@Component
public class ErpProviderMapper extends MapperAdapter<MybatisErpProviderMapper, ErpProviderPO> {
}

