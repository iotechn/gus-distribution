package com.dobbinsoft.gus.distribution.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dobbinsoft.gus.common.model.vo.PageResult;
import com.dobbinsoft.gus.distribution.data.dto.erp.ErpProviderCreateDTO;
import com.dobbinsoft.gus.distribution.data.dto.erp.ErpProviderUpdateDTO;
import com.dobbinsoft.gus.distribution.data.exception.DistributionErrorCode;
import com.dobbinsoft.gus.distribution.data.po.ErpProviderPO;
import com.dobbinsoft.gus.distribution.data.vo.erp.ErpProviderPageVO;
import com.dobbinsoft.gus.distribution.data.vo.erp.ErpProviderVO;
import com.dobbinsoft.gus.distribution.mapper.ErpProviderMapper;
import com.dobbinsoft.gus.distribution.service.ErpProviderService;
import com.dobbinsoft.gus.distribution.utils.UuidWorker;
import com.dobbinsoft.gus.web.exception.BasicErrorCode;
import com.dobbinsoft.gus.web.exception.ServiceException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ErpProviderServiceImpl implements ErpProviderService {

    private final ErpProviderMapper erpProviderMapper;

    @Override
    public PageResult<ErpProviderPageVO> page(Integer pageNum, Integer pageSize) {
        int pageNumber = (pageNum == null || pageNum <= 0) ? 1 : pageNum;
        int size = (pageSize == null || pageSize <= 0) ? 10 : pageSize;

        Page<ErpProviderPO> page = new Page<>(pageNumber, size);
        Page<ErpProviderPO> resultPage = erpProviderMapper.selectPage(page, new QueryWrapper<>());

        List<ErpProviderPageVO> voList = new ArrayList<>();
        for (ErpProviderPO po : resultPage.getRecords()) {
            ErpProviderPageVO vo = new ErpProviderPageVO();
            BeanUtils.copyProperties(po, vo);
            voList.add(vo);
        }

        return PageResult.<ErpProviderPageVO>builder()
                .totalCount(resultPage.getTotal())
                .totalPages(resultPage.getPages())
                .pageNumber((int) resultPage.getCurrent())
                .pageSize((int) resultPage.getSize())
                .hasMore(resultPage.hasNext())
                .data(voList)
                .build();
    }

    @Override
    public ErpProviderVO get(String id) {
        ErpProviderPO erpProviderPO = erpProviderMapper.selectById(id);
        if (erpProviderPO == null) {
            throw new ServiceException(BasicErrorCode.NO_RESOURCE);
        }

        ErpProviderVO vo = new ErpProviderVO();
        BeanUtils.copyProperties(erpProviderPO, vo);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ErpProviderVO create(ErpProviderCreateDTO createDTO) {
        // 检查是否已存在
        Long count = erpProviderMapper.selectCount(new QueryWrapper<>());
        if (count > 0) {
            throw new ServiceException(DistributionErrorCode.ERP_PROVIDER_ALREADY_EXISTS);
        }

        ErpProviderPO erpProviderPO = new ErpProviderPO();
        BeanUtils.copyProperties(createDTO, erpProviderPO);

        // 生成ID
        erpProviderPO.setId(UuidWorker.nextId());

        erpProviderMapper.insert(erpProviderPO);

        ErpProviderVO vo = new ErpProviderVO();
        BeanUtils.copyProperties(erpProviderPO, vo);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ErpProviderVO update(String id, ErpProviderUpdateDTO updateDTO) {
        ErpProviderPO existingPO = erpProviderMapper.selectById(id);
        if (existingPO == null) {
            throw new ServiceException(BasicErrorCode.NO_RESOURCE, "ERP提供商不存在，请先创建");
        }

        // 更新字段
        if (updateDTO.getType() != null) {
            existingPO.setType(updateDTO.getType());
        }
        if (updateDTO.getConfig() != null) {
            existingPO.setConfig(updateDTO.getConfig());
        }
        if (updateDTO.getRemark() != null) {
            existingPO.setRemark(updateDTO.getRemark());
        }

        erpProviderMapper.updateById(existingPO);

        ErpProviderVO vo = new ErpProviderVO();
        BeanUtils.copyProperties(existingPO, vo);
        return vo;
    }
}
