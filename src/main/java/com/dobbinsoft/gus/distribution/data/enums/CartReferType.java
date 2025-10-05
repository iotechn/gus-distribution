package com.dobbinsoft.gus.distribution.data.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CartReferType implements BaseEnums<Integer> {

    USER(1, "用户购物车"),
    ;

    private final Integer code;
    private final String msg;

}
