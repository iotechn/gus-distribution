package com.dobbinsoft.gus.distribution.mapper.mybatis;

import com.dobbinsoft.gus.distribution.data.IMapper;
import com.dobbinsoft.gus.distribution.data.po.UserPO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MybatisUserMapper extends IMapper<UserPO> {
}
