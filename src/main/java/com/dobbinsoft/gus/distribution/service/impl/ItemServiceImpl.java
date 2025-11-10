package com.dobbinsoft.gus.distribution.service.impl;

import com.dobbinsoft.gus.common.model.vo.PageResult;
import com.dobbinsoft.gus.distribution.client.gus.product.ProductItemFeignClient;
import com.dobbinsoft.gus.distribution.client.gus.product.model.ItemSearchDTO;
import com.dobbinsoft.gus.distribution.client.gus.product.model.ItemVO;
import com.dobbinsoft.gus.distribution.service.ItemService;
import com.dobbinsoft.gus.web.exception.BasicErrorCode;
import com.dobbinsoft.gus.web.exception.ServiceException;
import com.dobbinsoft.gus.web.vo.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ProductItemFeignClient productItemFeignClient;

    @Override
    public PageResult<ItemVO> search(ItemSearchDTO searchDTO) {
        R<PageResult<ItemVO>> result = productItemFeignClient.search(searchDTO);
        if (!BasicErrorCode.SUCCESS.getCode().equals(result.getCode())) {
            throw new ServiceException(result.getCode(), result.getMessage());
        }
        return result.getData();
    }

    @Override
    public ItemVO getBySmc(String smc) {
        R<ItemVO> result = productItemFeignClient.getBySmc(smc);
        if (!BasicErrorCode.SUCCESS.getCode().equals(result.getCode())) {
            throw new ServiceException(result.getCode(), result.getMessage());
        }
        return result.getData();
    }

    @Override
    public ItemVO getBySku(String sku) {
        R<ItemVO> result = productItemFeignClient.getBySku(sku);
        if (!BasicErrorCode.SUCCESS.getCode().equals(result.getCode())) {
            throw new ServiceException(result.getCode(), result.getMessage());
        }
        return result.getData();
    }
}

