package com.dobbinsoft.gus.distribution.client.gus.product.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class UnitGroupVO {

    private String id;
    private String name;
    private List<UnitVO> units;

    @Getter
    @Setter
    public static class UnitVO {
        private String name;
        private Boolean basic;
        private BigDecimal rate;
    }
} 