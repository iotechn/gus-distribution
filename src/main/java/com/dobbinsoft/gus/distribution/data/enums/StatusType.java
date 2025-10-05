package com.dobbinsoft.gus.distribution.data.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatusType implements BaseEnums<Integer> {

    DISABLED(0, "已失效"),
    ENABLED(1, "可用"),
    ;

    private final Integer code;
    private final String msg;

    public static StatusType getByCode(Integer code) {
        for (StatusType status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

}
