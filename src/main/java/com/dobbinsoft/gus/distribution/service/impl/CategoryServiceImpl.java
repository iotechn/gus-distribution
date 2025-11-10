package com.dobbinsoft.gus.distribution.service.impl;

import com.dobbinsoft.gus.distribution.client.gus.product.ProductCategoryFeignClient;
import com.dobbinsoft.gus.distribution.client.gus.product.model.CategoryVO;
import com.dobbinsoft.gus.distribution.service.CategoryService;
import com.dobbinsoft.gus.web.exception.BasicErrorCode;
import com.dobbinsoft.gus.web.exception.ServiceException;
import com.dobbinsoft.gus.web.vo.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private ProductCategoryFeignClient productCategoryFeignClient;

    @Override
    public List<CategoryVO> getCategoryTree() {
        R<List<CategoryVO>> result = productCategoryFeignClient.getCategoryTree();
        if (!BasicErrorCode.SUCCESS.getCode().equals(result.getCode())) {
            throw new ServiceException(result.getCode(), result.getMessage());
        }
        return result.getData();
    }
}

