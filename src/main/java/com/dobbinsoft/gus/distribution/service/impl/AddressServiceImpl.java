package com.dobbinsoft.gus.distribution.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.dobbinsoft.gus.distribution.data.dto.address.AddressQueryDTO;
import com.dobbinsoft.gus.distribution.data.dto.address.AddressUpsertDTO;
import com.dobbinsoft.gus.distribution.data.dto.session.FoSessionInfoDTO;
import com.dobbinsoft.gus.web.exception.ServiceException;
import com.dobbinsoft.gus.distribution.data.po.AddressPO;
import com.dobbinsoft.gus.distribution.data.vo.address.AddressVO;
import com.dobbinsoft.gus.distribution.mapper.AddressMapper;
import com.dobbinsoft.gus.distribution.service.AddressService;
import com.dobbinsoft.gus.distribution.utils.SessionUtils;
import com.dobbinsoft.gus.distribution.utils.UuidWorker;
import com.dobbinsoft.gus.web.exception.BasicErrorCode;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressMapper addressMapper;

    @Override
    @Transactional
    public AddressVO create(AddressUpsertDTO upsertDTO) {
        // 获取当前用户
        FoSessionInfoDTO sessionInfo = SessionUtils.getFoSession();

        AddressPO addressPO = new AddressPO();
        BeanUtils.copyProperties(upsertDTO, addressPO);
        
        // 生成ID
        addressPO.setId(UuidWorker.nextId());
        
        // 设置用户ID
        addressPO.setUserId(sessionInfo.getUserId());
        
        // 设置创建人
        addressPO.setCreatedBy(sessionInfo.getUserId());
        addressPO.setModifiedBy(sessionInfo.getUserId());
        
        // 如果设置为默认地址，需要将其他地址设为非默认
        if (Boolean.TRUE.equals(upsertDTO.getIsDefault())) {
            clearDefaultAddress(sessionInfo.getUserId());
        }
        
        addressMapper.insert(addressPO);
        
        return addressPO.convertToVO();
    }

    @Override
    @Transactional
    public AddressVO update(String id, AddressUpsertDTO upsertDTO) {
        // 获取当前用户
        FoSessionInfoDTO sessionInfo = SessionUtils.getFoSession();

        AddressPO addressPO = addressMapper.selectById(id);
        if (addressPO == null) {
            throw new ServiceException(BasicErrorCode.NO_RESOURCE);
        }
        
        // 验证地址是否属于当前用户
        if (!sessionInfo.getUserId().equals(addressPO.getUserId())) {
            throw new ServiceException(BasicErrorCode.NO_PERMISSION);
        }
        
        // 全量更新字段
        BeanUtils.copyProperties(upsertDTO, addressPO);
        
        // 如果设置为默认地址，需要将其他地址设为非默认
        if (Boolean.TRUE.equals(upsertDTO.getIsDefault())) {
            clearDefaultAddress(sessionInfo.getUserId());
        }
        
        addressPO.setModifiedBy(sessionInfo.getUserId());
        addressMapper.updateById(addressPO);
        
        return addressPO.convertToVO();
    }

    @Override
    @Transactional
    public void delete(String id) {
        // 获取当前用户
        FoSessionInfoDTO sessionInfo = SessionUtils.getFoSession();

        AddressPO addressPO = addressMapper.selectById(id);
        if (addressPO == null) {
            throw new ServiceException(BasicErrorCode.NO_RESOURCE);
        }
        
        // 验证地址是否属于当前用户
        if (!sessionInfo.getUserId().equals(addressPO.getUserId())) {
            throw new ServiceException(BasicErrorCode.NO_PERMISSION);
        }
        
        // 删除地址
        addressMapper.deleteById(id);
        
        // 如果删除的是默认地址，且还有其他地址，则选择最后一个添加的地址为默认地址
        if (Boolean.TRUE.equals(addressPO.getIsDefault())) {
            LambdaQueryWrapper<AddressPO> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(AddressPO::getUserId, sessionInfo.getUserId())
                    .orderByDesc(AddressPO::getId)
                    .last("LIMIT 1");
            
            AddressPO lastAddress = addressMapper.selectOne(wrapper);
            if (lastAddress != null) {
                lastAddress.setIsDefault(true);
                lastAddress.setModifiedBy(sessionInfo.getUserId());
                addressMapper.updateById(lastAddress);
            }
        }
    }

    @Override
    public AddressVO getById(String id) {
        // 获取当前用户
        FoSessionInfoDTO sessionInfo = SessionUtils.getFoSession();

        AddressPO addressPO = addressMapper.selectById(id);
        if (addressPO == null) {
            throw new ServiceException(BasicErrorCode.NO_RESOURCE);
        }
        
        // 验证地址是否属于当前用户
        if (!sessionInfo.getUserId().equals(addressPO.getUserId())) {
            throw new ServiceException(BasicErrorCode.NO_PERMISSION);
        }
        
        return addressPO.convertToVO();
    }

    @Override
    public List<AddressVO> query(AddressQueryDTO queryDTO) {
        // 获取当前用户
        FoSessionInfoDTO sessionInfo = SessionUtils.getFoSession();

        LambdaQueryWrapper<AddressPO> wrapper = new LambdaQueryWrapper<>();
        
        // 前台用户只能查询自己的地址，后台管理员可以查询指定用户的地址
        String targetUserId = queryDTO.getUserId();
        if (targetUserId == null) {
            // 前台用户查询自己的地址
            wrapper.eq(AddressPO::getUserId, sessionInfo.getUserId());
        } else {
            // 后台管理员查询指定用户的地址（这里可以添加权限验证）
            wrapper.eq(AddressPO::getUserId, targetUserId);
        }
        
        if (StringUtils.hasText(queryDTO.getUserName())) {
            wrapper.like(AddressPO::getUserName, queryDTO.getUserName());
        }
        if (StringUtils.hasText(queryDTO.getTelNumber())) {
            wrapper.eq(AddressPO::getTelNumber, queryDTO.getTelNumber());
        }
        if (StringUtils.hasText(queryDTO.getProvinceName())) {
            wrapper.eq(AddressPO::getProvinceName, queryDTO.getProvinceName());
        }
        if (StringUtils.hasText(queryDTO.getCityName())) {
            wrapper.eq(AddressPO::getCityName, queryDTO.getCityName());
        }
        if (StringUtils.hasText(queryDTO.getCountyName())) {
            wrapper.eq(AddressPO::getCountyName, queryDTO.getCountyName());
        }
        if (queryDTO.getIsDefault() != null) {
            wrapper.eq(AddressPO::getIsDefault, queryDTO.getIsDefault());
        }
        if (StringUtils.hasText(queryDTO.getLabel())) {
            wrapper.eq(AddressPO::getLabel, queryDTO.getLabel());
        }
        
        // 默认地址在前，按创建时间倒序
        wrapper.orderByDesc(AddressPO::getIsDefault);
        wrapper.orderByDesc(AddressPO::getCreatedTime);
        
        List<AddressPO> addressPOList = addressMapper.selectList(wrapper);
        return addressPOList.stream()
                .map(AddressPO::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public AddressVO getDefaultAddress() {
        // 获取当前用户
        FoSessionInfoDTO sessionInfo = SessionUtils.getFoSession();

        LambdaQueryWrapper<AddressPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AddressPO::getUserId, sessionInfo.getUserId())
                .eq(AddressPO::getIsDefault, true);
        
        AddressPO addressPO = addressMapper.selectOne(wrapper);
        if (addressPO == null) {
            return null;
        }
        
        return addressPO.convertToVO();
    }

    /**
     * 清除用户的默认地址
     */
    private void clearDefaultAddress(String userId) {
        LambdaUpdateWrapper<AddressPO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(AddressPO::getUserId, userId)
                .eq(AddressPO::getIsDefault, true)
                .set(AddressPO::getIsDefault, false);
        addressMapper.update(null, updateWrapper);
    }
} 