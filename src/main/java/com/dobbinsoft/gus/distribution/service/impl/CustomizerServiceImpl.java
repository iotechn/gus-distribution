package com.dobbinsoft.gus.distribution.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dobbinsoft.gus.common.model.vo.PageResult;
import com.dobbinsoft.gus.distribution.data.dto.customizer.CustomizerCreateDTO;
import com.dobbinsoft.gus.distribution.data.dto.customizer.CustomizerSearchDTO;
import com.dobbinsoft.gus.distribution.data.dto.customizer.CustomizerUpdateDTO;
import com.dobbinsoft.gus.distribution.data.po.CustomizerPO;
import com.dobbinsoft.gus.distribution.data.vo.customizer.CustomizerVO;
import com.dobbinsoft.gus.distribution.mapper.CustomizerMapper;
import com.dobbinsoft.gus.distribution.service.CustomizerService;
import com.dobbinsoft.gus.distribution.utils.UuidWorker;
import com.dobbinsoft.gus.web.exception.BasicErrorCode;
import com.dobbinsoft.gus.web.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomizerServiceImpl implements CustomizerService {

    private final CustomizerMapper customizerMapper;

    @Override
    public PageResult<CustomizerVO> page(CustomizerSearchDTO searchDTO) {
        Page<CustomizerPO> page = new Page<>(searchDTO.getPageNum(), searchDTO.getPageSize());
        
        LambdaQueryWrapper<CustomizerPO> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(searchDTO.getName())) {
            queryWrapper.like(CustomizerPO::getName, searchDTO.getName());
        }
        if (searchDTO.getStatus() != null) {
            queryWrapper.eq(CustomizerPO::getStatus, searchDTO.getStatus());
        }
        queryWrapper.orderByDesc(CustomizerPO::getCreatedTime);
        
        Page<CustomizerPO> resultPage = customizerMapper.selectPage(page, queryWrapper);
        
        List<CustomizerVO> voList = new ArrayList<>();
        for (CustomizerPO po : resultPage.getRecords()) {
            CustomizerVO vo = new CustomizerVO();
            BeanUtils.copyProperties(po, vo);
            voList.add(vo);
        }

        return PageResult.<CustomizerVO>builder()
                .totalCount(page.getTotal())
                .totalPages(page.getPages())
                .pageNumber((int) page.getCurrent())
                .pageSize((int) page.getSize())
                .hasMore(page.hasNext())
                .data(voList)
                .build();
    }

    @Override
    public CustomizerVO detail(String id) {
        CustomizerPO customizerPO = customizerMapper.selectById(id);
        if (customizerPO == null) {
            throw new ServiceException(BasicErrorCode.NO_RESOURCE);
        }
        
        CustomizerVO detailVO = new CustomizerVO();
        BeanUtils.copyProperties(customizerPO, detailVO);
        
        return detailVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CustomizerVO create(CustomizerCreateDTO createDTO) {
        CustomizerPO customizerPO = new CustomizerPO();
        BeanUtils.copyProperties(createDTO, customizerPO);
        
        // 生成ID
        customizerPO.setId(UuidWorker.nextId());
        
        customizerMapper.insert(customizerPO);
        
        return detail(customizerPO.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CustomizerVO update(String id, CustomizerUpdateDTO updateDTO) {
        CustomizerPO existingPO = customizerMapper.selectById(id);
        if (existingPO == null) {
            throw new ServiceException(BasicErrorCode.NO_RESOURCE);
        }

        existingPO.setName(updateDTO.getName());
        existingPO.setStatus(updateDTO.getStatus());
        existingPO.setContent(updateDTO.getContent());
        
        customizerMapper.updateById(existingPO);
        
        return detail(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String id) {
        CustomizerPO existingPO = customizerMapper.selectById(id);
        if (existingPO == null) {
            throw new ServiceException(BasicErrorCode.NO_RESOURCE);
        }
        
        // 删除自定义页面
        customizerMapper.deleteById(id);
    }
}
