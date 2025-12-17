package com.dobbinsoft.gus.distribution.client.erp;

import java.util.List;

import com.dobbinsoft.gus.distribution.client.erp.model.ErpCategory;
import com.dobbinsoft.gus.distribution.client.erp.model.ErpEvent;
import com.dobbinsoft.gus.distribution.client.erp.model.ErpItem;
import com.dobbinsoft.gus.distribution.client.erp.model.ErpItemAttr;
import com.dobbinsoft.gus.distribution.client.erp.model.ErpItemMapping;
import com.dobbinsoft.gus.distribution.client.erp.model.ErpStock;
import com.dobbinsoft.gus.distribution.client.erp.model.ErpUnitGroup;
import com.dobbinsoft.gus.distribution.data.po.ErpProviderPO;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Erp对接客户端
 * 职能：
 * 1. 通过ERP同步分类、单位、商品
 * 2. 接受ERP通知：
 * 3.1. 接受ERP库存更改通知
 */
public interface ErpClient {

    /**
     * 获取所有分类
     * @param properties
     * @return
     */
    List<ErpCategory> getCategories(ErpProviderPO erpProviderPO);

    /**
     * 单位
     * @param properties
     * @return
     */
    List<ErpUnitGroup> getUnits(ErpProviderPO erpProviderPO);

    /**
     * 获取商品结构
     * @param smc 商品结构可指定SMC(可空)
     * @param properties
     * @return
     */
    ErpItemAttr getItemAttr(String smc, ErpProviderPO erpProviderPO);

    /**
     * 获取商品
     * @param mappings
     * @param properties
     * @return
     */
    List<ErpItem> getItems(List<ErpItemMapping> mappings, ErpProviderPO erpProviderPO);

    /**
     * 获取库存
     * @param sku 指定SKU的库存
     * @param locationCode 指定仓库的库存
     * @param properties
     * @return
     */
    List<ErpStock> getStocks(String sku, String locationCode, ErpProviderPO erpProviderPO);

    /**
     * 校验回调合法性
     * @param request
     * @param body
     * @param erpProviderPO
     * @return
     */
    boolean validateCallback(HttpServletRequest request, String body, ErpProviderPO erpProviderPO);

    /**
     * 将请求转化为标准的ERP事件
     * @param request
     * @param body
     * @return
     */
    List<ErpEvent> convertToEvents(HttpServletRequest request, String body);

}
