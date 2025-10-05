package com.dobbinsoft.gus.distribution.utils;

import com.github.f4b6a3.uuid.UuidCreator;
import lombok.extern.slf4j.Slf4j;

/**
 * 用于获取UUID的主键
 */
@Slf4j
public class UuidWorker {

    //下一个ID生成算法
    public static String nextId() {
        return UuidCreator.getTimeOrderedWithRandom().toString();
    }
}
