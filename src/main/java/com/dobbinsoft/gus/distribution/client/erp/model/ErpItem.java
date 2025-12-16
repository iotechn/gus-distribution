package com.dobbinsoft.gus.distribution.client.erp.model;

import java.math.BigDecimal;
import java.util.List;

import com.dobbinsoft.gus.distribution.client.gus.product.model.ItemStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErpItem {

    private String erpId;

    private String smc;

    private ItemStatus status;

    private List<String> erpCategoryIds;

    private String erpUnitGroupId;

    private List<String> images;

    // 可自定义 映射 的字段

    private String name;

    private BigDecimal price;

    private String description;

    private String richText;

}
