package com.dobbinsoft.gus.distribution.data;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.Collection;

/**
 * Description: 所有Mapper的基类
 *
 * @param <T> 实体类
 */
public interface IMapper<T> extends BaseMapper<T> {

    /**
     * 高效率批量插入 仅支持 MYSQL
     *
     * @param entityList 实体列表
     * @return 成功插入数量
     */
    Integer insertBatchSomeColumn(Collection<T> entityList);

}
