package com.dobbinsoft.gus.distribution.client.erp.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class ErpUnitGroup {

    /**
     * 单位ID
     */
    private String erpId;

    /**
     * 单位名称
     */
    private String name;

    /**
     * 单位条目
     */
    private List<ErpUnitEntry> entries;

    /**
     * ERP单位条目
     */
    @Data
    public static class ErpUnitEntry {
        /**
         * 条目ID
         */
        private String erpId;

        /**
         * 条目名称
         */
        private String name;

        /**
         * 比率(第一个单位比率必须为1）代表主单位
         */
        private BigDecimal rate;

    }

}
