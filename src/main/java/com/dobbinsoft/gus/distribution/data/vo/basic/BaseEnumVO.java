package com.dobbinsoft.gus.distribution.data.vo.basic;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class BaseEnumVO {

    @Schema(description = "枚举类名")
    private String enumName;

    @Schema(description = "枚举列表")
    private List<Item> items;

    @Getter
    @Setter
    public static class Item {

        private Serializable code;

        private String desc;

    }

}
