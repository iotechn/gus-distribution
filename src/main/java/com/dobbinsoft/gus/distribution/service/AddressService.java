package com.dobbinsoft.gus.distribution.service;

import com.dobbinsoft.gus.distribution.data.dto.address.AddressQueryDTO;
import com.dobbinsoft.gus.distribution.data.dto.address.AddressUpsertDTO;
import com.dobbinsoft.gus.distribution.data.vo.address.AddressVO;

import java.util.List;

public interface AddressService {

    /**
     * 创建地址
     */
    AddressVO create(AddressUpsertDTO upsertDTO);

    /**
     * 更新地址
     */
    AddressVO update(String id, AddressUpsertDTO upsertDTO);

    /**
     * 删除地址
     */
    void delete(String id);

    /**
     * 根据ID获取地址
     */
    AddressVO getById(String id);

    /**
     * 查询用户地址列表
     */
    List<AddressVO> query(AddressQueryDTO queryDTO);

    /**
     * 获取用户默认地址
     */
    AddressVO getDefaultAddress();
} 