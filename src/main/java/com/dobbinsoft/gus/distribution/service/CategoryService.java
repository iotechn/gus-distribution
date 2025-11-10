package com.dobbinsoft.gus.distribution.service;

import com.dobbinsoft.gus.distribution.client.gus.product.model.CategoryVO;

import java.util.List;

public interface CategoryService {

    /**
     * 获取分类树
     */
    List<CategoryVO> getCategoryTree();
}

