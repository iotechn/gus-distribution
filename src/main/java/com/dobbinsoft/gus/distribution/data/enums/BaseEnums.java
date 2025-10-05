package com.dobbinsoft.gus.distribution.data.enums;

import org.reflections.Reflections;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

    /**
     * 获取所有实现了BaseEnums接口的枚举类
     * @return 实现了BaseEnums接口的枚举类列表
     */
    static <S extends Serializable> List<Class<? extends BaseEnums<S>>> getAllEnumClasses() {
        List<Class<? extends BaseEnums<S>>> enumClasses = new ArrayList<>();
        try {
            Reflections reflections = new Reflections("com.kering.cus.remotesales.data.enums");
            Set<Class<? extends BaseEnums>> subTypes = reflections.getSubTypesOf(BaseEnums.class);
            for (Class<? extends BaseEnums> clazz : subTypes) {
                if (clazz.isEnum()) {
                    enumClasses.add((Class<? extends BaseEnums<S>>) clazz);
                }
            }
        } catch (Exception e) {

        }
        return enumClasses;
    }

}
