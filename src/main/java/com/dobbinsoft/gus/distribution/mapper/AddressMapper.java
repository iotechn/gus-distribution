package com.dobbinsoft.gus.distribution.mapper;

import com.dobbinsoft.gus.distribution.data.po.AddressPO;
import com.dobbinsoft.gus.distribution.mapper.mybatis.MybatisAddressMapper;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper extends MapperAdapter<MybatisAddressMapper, AddressPO> {
}
