package com.dobbinsoft.gus.distribution.mapper;

import com.dobbinsoft.gus.distribution.data.po.CustomizerPO;
import com.dobbinsoft.gus.distribution.mapper.mybatis.MybatisCustomizerMapper;
import org.springframework.stereotype.Component;

@Component
public class CustomizerMapper extends MapperAdapter<MybatisCustomizerMapper, CustomizerPO> {
}

