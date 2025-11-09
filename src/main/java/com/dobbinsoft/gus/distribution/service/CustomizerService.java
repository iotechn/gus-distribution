package com.dobbinsoft.gus.distribution.service;

import com.dobbinsoft.gus.common.model.vo.PageResult;
import com.dobbinsoft.gus.distribution.data.dto.customizer.CustomizerCreateDTO;
import com.dobbinsoft.gus.distribution.data.dto.customizer.CustomizerSearchDTO;
import com.dobbinsoft.gus.distribution.data.dto.customizer.CustomizerUpdateDTO;
import com.dobbinsoft.gus.distribution.data.vo.customizer.CustomizerVO;

public interface CustomizerService {

    /**
     * 分页查询自定义页面
     */
    PageResult<CustomizerVO> page(CustomizerSearchDTO searchDTO);

    /**
     * 根据ID获取自定义页面详情
     */
    CustomizerVO detail(String id);

    /**
     * 创建自定义页面
     */
    CustomizerVO create(CustomizerCreateDTO createDTO);

    /**
     * 更新自定义页面
     */
    CustomizerVO update(String id, CustomizerUpdateDTO updateDTO);

    /**
     * 删除自定义页面
     */
    void delete(String id);

    /**
     * 获取租户第一个自定义页面
     */
    CustomizerVO first();

}
