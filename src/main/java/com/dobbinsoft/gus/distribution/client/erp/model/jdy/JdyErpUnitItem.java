package com.dobbinsoft.gus.distribution.client.erp.model.jdy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.dobbinsoft.gus.distribution.client.erp.model.ErpUnitGroup;

import lombok.Data;

/**
 * ERP单位项
 */
@Data
public class JdyErpUnitItem {
    /**
     * 单位ID
     */
    private Long id;

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
        private Long id;

        /**
         * 条目名称
         */
        private String name;
    }

    public ErpUnitGroup toStandard() {
        ErpUnitGroup erpUnitGroup = new ErpUnitGroup();
        erpUnitGroup.setErpId(this.id.toString());
        // 打,箱(1:4),
        erpUnitGroup.setName(this.name);

        List<ErpUnitGroup.ErpUnitEntry> erpEntries = new ArrayList<>();
        // 从注释中提取比例信息：打,箱(1:4) -> [1, 4]
        int[] rates = extractRatesFromComment(this.name);
        
        for (int i = 0; i < entries.size(); i++) {
            ErpUnitEntry entry = entries.get(i);
            ErpUnitGroup.ErpUnitEntry erpUnitEntry = new ErpUnitGroup.ErpUnitEntry();
            erpUnitEntry.setErpId(entry.getId().toString());
            erpUnitEntry.setName(entry.getName());
            // 设置比例，如果数组长度不够则默认为1
            erpUnitEntry.setRate(new BigDecimal(i < rates.length ? rates[i] : 1));
            erpEntries.add(erpUnitEntry);
        }

        erpUnitGroup.setEntries(erpEntries);
        return erpUnitGroup;
    }

    /**
     * 从注释中提取比例信息
     * @param comment 注释字符串，如 "打,箱(1:4)"
     * @return 比例数组，如 [1, 4]
     */
    private int[] extractRatesFromComment(String comment) {
        try {
            // 查找括号中的内容
            int start = comment.indexOf('(');
            int end = comment.indexOf(')');
            if (start != -1 && end != -1 && start < end) {
                String ratesStr = comment.substring(start + 1, end);
                // 按冒号分割比例
                String[] rateParts = ratesStr.split(":");
                int[] rates = new int[rateParts.length];
                for (int i = 0; i < rateParts.length; i++) {
                    rates[i] = Integer.parseInt(rateParts[i].trim());
                }
                return rates;
            }
        } catch (Exception e) {
            // 如果解析失败，记录日志或使用默认值
        }
        // 默认返回 [1] 表示主单位
        return new int[]{1};
    }

} 