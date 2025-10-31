package com.dobbinsoft.gus.distribution.service.impl;

import com.dobbinsoft.gus.common.model.vo.PageResult;
import com.dobbinsoft.gus.distribution.client.configcenter.ConfigCenterClient;
import com.dobbinsoft.gus.distribution.client.configcenter.vo.ConfigContentPublicVO;
import com.dobbinsoft.gus.distribution.client.configcenter.vo.ConfigContentVO;
import com.dobbinsoft.gus.distribution.client.gus.location.LocationFeignClient;
import com.dobbinsoft.gus.distribution.client.gus.location.model.LocationVO;
import com.dobbinsoft.gus.distribution.data.vo.config.ConfigContentPublicExtVO;
import com.dobbinsoft.gus.distribution.service.OpenConfigService;
import com.dobbinsoft.gus.web.exception.BasicErrorCode;
import com.dobbinsoft.gus.web.exception.ServiceException;
import com.dobbinsoft.gus.web.vo.R;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class OpenConfigServiceImpl implements OpenConfigService {

    private static final int PAGE_SIZE = 200;

    @Autowired
    private ConfigCenterClient configCenterClient;

    @Autowired
    private LocationFeignClient locationFeignClient;

    @Override
    public ConfigContentPublicExtVO getPublicConfigWithLocations() {
        ConfigContentVO configContentVO = configCenterClient.getBrandAllConfigContent();
        ConfigContentPublicVO publicVO = configContentVO.toPublicVO();

        ConfigContentPublicExtVO result = new ConfigContentPublicExtVO();
        BeanUtils.copyProperties(publicVO, result);

        // 查询门店，仅取启用的；如果开启虚拟仓，仅查询虚拟类型
        LocationVO.Status status = LocationVO.Status.ENABLED;
        LocationVO.Type type = Boolean.TRUE.equals(publicVO.getEnableVirtualLocation()) ? LocationVO.Type.VIRTUAL : null;

        // 分页拉取，单页200
        new java.util.ArrayList<>();
        List<LocationVO> all = new java.util.ArrayList<>();
        int pageNum = 1;
        while (true) {
            R<PageResult<LocationVO>> response = locationFeignClient.page(pageNum, PAGE_SIZE, status, type);
            if (!BasicErrorCode.SUCCESS.getCode().equals(response.getCode()) || response.getData() == null) {
                throw new ServiceException(response.getCode(), response.getMessage());
            }
            PageResult<LocationVO> page = response.getData();
            List<LocationVO> batch = page.getData() == null ? Collections.emptyList() : page.getData();
            if (batch.isEmpty()) {
                break;
            }
            all.addAll(batch);
            if (batch.size() < PAGE_SIZE) {
                break;
            }
            pageNum++;
        }
        result.setLocations(all);

        return result;
    }
}


