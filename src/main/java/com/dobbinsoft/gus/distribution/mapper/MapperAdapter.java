package com.dobbinsoft.gus.distribution.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dobbinsoft.gus.distribution.data.po.Pk;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public class MapperAdapter<M extends BaseMapper<T>, T extends Pk> {

    @Autowired
    private M baseMapper;

    // 读操作
    public List<T> selectList(QueryWrapper<T> queryWrapper) {
        return this.baseMapper.selectList(queryWrapper);
    }

    public T selectOne(QueryWrapper<T> queryWrapper) {
        List<T> ts = this.baseMapper.selectList(queryWrapper);
        if (ts.isEmpty()) {
            return null;
        }
        if (ts.size() > 1) {
            throw new RuntimeException("select one more than one entity");
        }
        return ts.getFirst();
    }

    public T selectById(Serializable id) {
        return this.selectOne(new QueryWrapper<T>().eq("id", id));
    }

    public T selectById(Serializable id, String idName) {
        return this.selectOne(new QueryWrapper<T>().eq(idName, id));
    }

    // 写操作
    public int insert(T entity) {
        return baseMapper.insert(entity);
    }

    public int updateById(T entity) {
        return baseMapper.update(entity, new QueryWrapper<T>().eq("id", entity.pk()));
    }

    public int updateById(T entity, String idName) {
        return baseMapper.update(entity, new QueryWrapper<T>().eq(idName, entity.pk()));
    }

    public int update(T entity, QueryWrapper<T> updateWrapper) {
        return baseMapper.update(entity, updateWrapper);
    }

    public int delete(QueryWrapper<T> queryWrapper) {
        return baseMapper.delete(queryWrapper);
    }

    public int deleteById(Serializable id) {
        return baseMapper.deleteById(id);
    }

    public int deleteByIds(Collection<? extends Serializable> ids) {
        return baseMapper.deleteByIds(ids);
    }

    // 统计
    public Long selectCount(QueryWrapper<T> queryWrapper) {
        return baseMapper.selectCount(queryWrapper);
    }

    // 分页
    public <P extends IPage<T>> P selectPage(P page, QueryWrapper<T> queryWrapper) {
        return baseMapper.selectPage(page, queryWrapper);
    }

}
