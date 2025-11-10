package com.dobbinsoft.gus.distribution.service;

import com.dobbinsoft.gus.common.model.vo.PageResult;
import com.dobbinsoft.gus.distribution.client.gus.product.model.ItemSearchDTO;
import com.dobbinsoft.gus.distribution.client.gus.product.model.ItemVO;

public interface ItemService {

    /**
     * 搜索商品
     */
    PageResult<ItemVO> search(ItemSearchDTO searchDTO);

    /**
     * 根据SMC获取商品
     */
    ItemVO getBySmc(String smc);

    /**
     * 根据SKU获取商品
     */
    ItemVO getBySku(String sku);
}

