package com.dobbinsoft.gus.distribution.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dobbinsoft.gus.distribution.data.dto.social.SocialAuthenticatorCreateDTO;
import com.dobbinsoft.gus.distribution.data.dto.social.SocialAuthenticatorUpdateDTO;
import com.dobbinsoft.gus.distribution.data.enums.UserSrcType;
import com.dobbinsoft.gus.distribution.data.exception.DistributionErrorCode;
import com.dobbinsoft.gus.distribution.data.po.SocialAuthenticatorPO;
import com.dobbinsoft.gus.distribution.data.vo.social.SocialAuthenticatorVO;
import com.dobbinsoft.gus.distribution.mapper.SocialAuthenticatorMapper;
import com.dobbinsoft.gus.distribution.service.SocialAuthenticatorService;
import com.dobbinsoft.gus.distribution.utils.SessionUtils;
import com.dobbinsoft.gus.distribution.utils.UuidWorker;
import com.dobbinsoft.gus.web.exception.ServiceException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SocialAuthenticatorServiceImpl implements SocialAuthenticatorService {

    @Autowired
    private SocialAuthenticatorMapper socialAuthenticatorMapper;

    @Override
    @Transactional
    public SocialAuthenticatorVO create(SocialAuthenticatorCreateDTO createDTO) {
        // Check if authenticator already exists for this source type
        LambdaQueryWrapper<SocialAuthenticatorPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SocialAuthenticatorPO::getSrc, createDTO.getSrc());
        if (socialAuthenticatorMapper.selectCount(queryWrapper) > 0) {
            throw new ServiceException(DistributionErrorCode.SOCIAL_AUTHENTICATOR_ALREADY_EXISTS);
        }

        SocialAuthenticatorPO socialAuthenticatorPO = new SocialAuthenticatorPO();
        BeanUtils.copyProperties(createDTO, socialAuthenticatorPO);
        
        // Generate ID
        socialAuthenticatorPO.setId(UuidWorker.nextId());
        
        // Set audit fields
        String currentUser = SessionUtils.getBoSession() != null ? SessionUtils.getBoSession().getUserId() : "system";
        socialAuthenticatorPO.setCreatedBy(currentUser);
        socialAuthenticatorPO.setModifiedBy(currentUser);
        
        socialAuthenticatorMapper.insert(socialAuthenticatorPO);
        
        return convertToVO(socialAuthenticatorPO);
    }

    @Override
    @Transactional
    public SocialAuthenticatorVO update(SocialAuthenticatorUpdateDTO updateDTO) {
        SocialAuthenticatorPO existingPO = socialAuthenticatorMapper.selectById(updateDTO.getId());
        if (existingPO == null) {
            throw new ServiceException(DistributionErrorCode.SOCIAL_AUTHENTICATOR_NOT_FOUND);
        }

        // Check if another authenticator exists with the same source type (excluding current one)
        LambdaQueryWrapper<SocialAuthenticatorPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SocialAuthenticatorPO::getSrc, updateDTO.getSrc())
                   .ne(SocialAuthenticatorPO::getId, updateDTO.getId());
        if (socialAuthenticatorMapper.selectCount(queryWrapper) > 0) {
            throw new ServiceException(DistributionErrorCode.SOCIAL_AUTHENTICATOR_ALREADY_EXISTS);
        }

        BeanUtils.copyProperties(updateDTO, existingPO);
        
        // Set audit fields
        String currentUser = SessionUtils.getBoSession() != null ? SessionUtils.getBoSession().getUserId() : "system";
        existingPO.setModifiedBy(currentUser);
        
        socialAuthenticatorMapper.updateById(existingPO);
        
        return convertToVO(existingPO);
    }

    @Override
    @Transactional
    public void delete(String id) {
        SocialAuthenticatorPO socialAuthenticatorPO = socialAuthenticatorMapper.selectById(id);
        if (socialAuthenticatorPO == null) {
            throw new ServiceException(DistributionErrorCode.SOCIAL_AUTHENTICATOR_NOT_FOUND);
        }
        
        socialAuthenticatorMapper.deleteById(id);
    }

    @Override
    public SocialAuthenticatorVO getById(String id) {
        SocialAuthenticatorPO socialAuthenticatorPO = socialAuthenticatorMapper.selectById(id);
        if (socialAuthenticatorPO == null) {
            throw new ServiceException(DistributionErrorCode.SOCIAL_AUTHENTICATOR_NOT_FOUND);
        }
        
        return convertToVO(socialAuthenticatorPO);
    }

    @Override
    public List<SocialAuthenticatorVO> getAll() {
        LambdaQueryWrapper<SocialAuthenticatorPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(SocialAuthenticatorPO::getSrc);
        
        List<SocialAuthenticatorPO> poList = socialAuthenticatorMapper.selectList(queryWrapper);
        
        return poList.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    private SocialAuthenticatorVO convertToVO(SocialAuthenticatorPO po) {
        SocialAuthenticatorVO vo = new SocialAuthenticatorVO();
        BeanUtils.copyProperties(po, vo);
        
        // Mask the app secret for security (always mask in response)
        vo.setAppSecret("******");
        
        // Add source description
        UserSrcType srcType = UserSrcType.getByCode(po.getSrc());
        if (srcType != null) {
            vo.setSrcDesc(srcType.getMsg());
        }
        
        return vo;
    }
} 