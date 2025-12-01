package com.dobbinsoft.gus.distribution.mapper;

import com.dobbinsoft.gus.distribution.data.po.UserSocialPO;
import com.dobbinsoft.gus.distribution.mapper.mybatis.MybatisUserSocialMapper;
import org.springframework.stereotype.Component;

@Component
public class UserSocialMapper extends MapperAdapter<MybatisUserSocialMapper, UserSocialPO> {
}

