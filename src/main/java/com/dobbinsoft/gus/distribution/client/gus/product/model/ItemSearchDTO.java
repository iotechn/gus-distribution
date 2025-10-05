package com.dobbinsoft.gus.distribution.client.gus.product.model;

import com.dobbinsoft.gus.common.model.dto.PageSearchDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ItemSearchDTO extends PageSearchDTO {

    private String locationCode;

    private List<String> smc;

    private List<String> sku;

    private List<Long> categoryId;

    private Map<String, List<String>> attrs;

    private List<Sort> sort;

    public static class Sort {
        @Getter
        @Setter
        private SortField field;

        @Getter
        @Setter
        private SortOrder order;

    }

    public enum SortField {
        smc,
        @Schema(description = "if hasStock desc the \"location\" has stock items will be return in the head of page")
        hasStock,
        createTime,
        updateTime
    }

    public enum SortOrder {
        ASC,
        DESC
    }

}
