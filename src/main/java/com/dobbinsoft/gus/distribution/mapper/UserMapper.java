package com.dobbinsoft.gus.distribution.mapper;

import com.dobbinsoft.gus.distribution.data.po.UserPO;
import com.dobbinsoft.gus.distribution.mapper.mybatis.MybatisUserMapper;
import org.springframework.stereotype.Component;

@Component
public class UserMapper extends MapperAdapter<MybatisUserMapper, UserPO> {
}

