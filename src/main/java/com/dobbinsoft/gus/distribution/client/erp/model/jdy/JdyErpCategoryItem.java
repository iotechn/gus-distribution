package com.dobbinsoft.gus.distribution.client.erp.model.jdy;

import com.dobbinsoft.gus.distribution.client.erp.model.ErpCategory;

import lombok.Data;

/**
 * ERP分类项
 */
@Data
public class JdyErpCategoryItem {
    /**
     * 分类ID
     */
    private Long id;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 父分类ID
     */
    private Long parentId;

    public ErpCategory toStandard() {
        ErpCategory erpCategory = new ErpCategory();
        erpCategory.setErpId(this.id.toString());
        erpCategory.setName(this.name);
        erpCategory.setErpParentId(this.parentId.toString());
        return erpCategory;
    }

} 