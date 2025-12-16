package com.dobbinsoft.gus.distribution.client.erp.model.jdy;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

/**
 * ERP商品项
 */
@Data
public class JdyErpProductItem {
    /**
     * 商品ID
     */
    private Long id;

    /**
     * 商品编码
     */
    private String productNumber;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 单位名称
     */
    private String unitName;

    /**
     * 单位类型
     */
    private Long unitType;

    /**
     * 单位类型名称
     */
    private String unitTypeName;

    /**
     * 单位
     */
    private Long unit;

    /**
     * 零售价
     */
    private BigDecimal retailPrice;

    /**
     * VIP价格
     */
    private BigDecimal vipPrice;

    /**
     * 备注
     */
    private String remark;

    /**
     * 是否删除
     */
    private Boolean isDeleted;

    /**
     * 多图片
     */
    private List<ErpProductImage> multiImg;

    /**
     * ERP商品图片
     */
    @Data
    public static class ErpProductImage {
        /**
         * 图片URL
         */
        private String url;
    }
} 