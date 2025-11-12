package com.dobbinsoft.gus.distribution.service;

import com.dobbinsoft.gus.common.model.vo.PageResult;
import com.dobbinsoft.gus.distribution.client.gus.product.model.ItemSearchDTO;
import com.dobbinsoft.gus.distribution.data.vo.item.ItemWithStockVO;

public interface ItemService {

    /**
     * 搜索商品
     */
    PageResult<ItemWithStockVO> search(ItemSearchDTO searchDTO, String locationCode);

    /**
     * 根据SMC获取商品
     */
    ItemWithStockVO getBySmc(String smc, String locationCode);

    /**
     * 根据SKU获取商品
     */
    ItemWithStockVO getBySku(String sku, String locationCode);
}

