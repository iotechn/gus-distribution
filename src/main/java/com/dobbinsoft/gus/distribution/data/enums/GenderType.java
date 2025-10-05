package com.dobbinsoft.gus.distribution.data.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GenderType implements BaseEnums<Integer> {

    MALE(1, "男"),
    FEMALE(2, "女"),
    ;

    private final Integer code;
    private final String msg;

    public static GenderType getByCode(Integer code) {
        for (GenderType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
} 