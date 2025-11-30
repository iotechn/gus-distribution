package com.dobbinsoft.gus.distribution.data.enums;

import java.io.Serializable;

/**
 * ClassName: BaseEnums
 * Description: 基本枚举
 *
 * @param <S> 枚举代码类型
 */
public interface BaseEnums<S extends Serializable> {

    S getCode();

    String getMsg();

    static <S extends Serializable, T extends BaseEnums<S>> T getByCode(S s, Class<T> clazz) {
        BaseEnums<S>[] enumConstants = clazz.getEnumConstants();
        for (BaseEnums<S> baseEnums : enumConstants) {
            if (baseEnums.getCode().equals(s)) {
                return (T) baseEnums;
            }
        }
        return null;
    }

    static <T extends Serializable> String getMsgByCode(T t, Class<? extends BaseEnums<T>> clazz) {
        BaseEnums<T> baseEnums = getByCode(t, clazz);
        if (baseEnums == null) {
            return null;
        }
        return baseEnums.getMsg();
    }


}
