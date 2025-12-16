package com.dobbinsoft.gus.distribution.service;

import com.dobbinsoft.gus.common.model.vo.PageResult;
import com.dobbinsoft.gus.distribution.data.dto.erp.ErpProviderCreateDTO;
import com.dobbinsoft.gus.distribution.data.dto.erp.ErpProviderUpdateDTO;
import com.dobbinsoft.gus.distribution.data.vo.erp.ErpProviderPageVO;
import com.dobbinsoft.gus.distribution.data.vo.erp.ErpProviderVO;

public interface ErpProviderService {

    /**
     * 分页查询ERP提供商（虽然只允许存在一个，但提供分页接口便于前端统一处理）
     */
    PageResult<ErpProviderPageVO> page(Integer pageNum, Integer pageSize);

    /**
     * 根据ID获取ERP提供商
     */
    ErpProviderVO get(String id);

    /**
     * 创建ERP提供商（只允许创建一个）
     */
    ErpProviderVO create(ErpProviderCreateDTO createDTO);

    /**
     * 根据ID更新ERP提供商
     */
    ErpProviderVO update(String id, ErpProviderUpdateDTO updateDTO);
}
