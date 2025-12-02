package com.dobbinsoft.gus.distribution.service.impl;

import com.dobbinsoft.gus.common.model.vo.PageResult;
import com.dobbinsoft.gus.distribution.client.gus.product.ProductItemFeignClient;
import com.dobbinsoft.gus.distribution.client.gus.product.ProductStockFeignClient;
import com.dobbinsoft.gus.distribution.client.gus.product.model.*;
import com.dobbinsoft.gus.distribution.data.vo.item.ItemWithStockVO;
import com.dobbinsoft.gus.distribution.service.ItemService;
import com.dobbinsoft.gus.web.exception.BasicErrorCode;
import com.dobbinsoft.gus.web.exception.ServiceException;
import com.dobbinsoft.gus.web.vo.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ProductItemFeignClient productItemFeignClient;

    @Autowired
    private ProductStockFeignClient productStockFeignClient;

    @Override
    public PageResult<ItemWithStockVO> search(ItemSearchDTO searchDTO, String locationCode) {
        R<PageResult<ItemVO>> result = productItemFeignClient.search(searchDTO);
        if (!BasicErrorCode.SUCCESS.getCode().equals(result.getCode())) {
            throw new ServiceException(result.getCode(), result.getMessage());
        }
        PageResult<ItemVO> pageResult = result.getData();
        if (pageResult == null) {
            PageResult<ItemWithStockVO> emptyResult = new PageResult<>();
            emptyResult.setData(Collections.emptyList());
            emptyResult.setHasMore(Boolean.FALSE);
            emptyResult.setPageNumber(0);
            emptyResult.setPageSize(0);
            emptyResult.setTotalCount(0);
            emptyResult.setTotalPages(0);
            return emptyResult;
        }
        PageResult<ItemWithStockVO> converted = new PageResult<>();
        converted.setTotalCount(pageResult.getTotalCount());
        converted.setTotalPages(pageResult.getTotalPages());
        converted.setPageNumber(pageResult.getPageNumber());
        converted.setPageSize(pageResult.getPageSize());
        converted.setHasMore(pageResult.getHasMore());
        if (CollectionUtils.isEmpty(pageResult.getData())) {
            converted.setData(Collections.emptyList());
            return converted;
        }
        converted.setData(pageResult.getData().stream()
                .map(item -> buildItemWithStock(item, locationCode))
                .collect(Collectors.toList()));
        return converted;
    }

    @Override
    public ItemWithStockVO getBySmc(String smc, String locationCode) {
        R<ItemDetailVO> result = productItemFeignClient.getBySmc(smc);
        if (!BasicErrorCode.SUCCESS.getCode().equals(result.getCode())) {
            throw new ServiceException(result.getCode(), result.getMessage());
        }
        ItemDetailVO itemDetailVO = result.getData();
        if (itemDetailVO == null) {
            return null;
        }
        return buildItemWithStock(itemDetailVO, locationCode);
    }

    @Override
    public ItemWithStockVO getBySku(String sku, String locationCode) {
        R<ItemDetailVO> result = productItemFeignClient.getBySku(sku);
        if (!BasicErrorCode.SUCCESS.getCode().equals(result.getCode())) {
            throw new ServiceException(result.getCode(), result.getMessage());
        }
        ItemDetailVO itemDetailVO = result.getData();
        if (itemDetailVO == null) {
            return null;
        }
        return buildItemWithStock(itemDetailVO, locationCode);
    }

    /**
     *
     * @param itemVO 传入如果是 ItemVO 则 ItemWithStockVO只有 itemVo的属性 + Stock
     * @param locationCode
     * @return
     */
    private ItemWithStockVO buildItemWithStock(ItemVO itemVO, String locationCode) {
        ItemWithStockVO target = new ItemWithStockVO();
        BeanUtils.copyProperties(itemVO, target);
        if (!StringUtils.hasText(locationCode) || !StringUtils.hasText(itemVO.getSmc())) {
            return target;
        }
        target.setLocationCode(locationCode);
        R<ItemStockVO> stockResult = productStockFeignClient.itemStock(itemVO.getSmc());
        if (!BasicErrorCode.SUCCESS.getCode().equals(stockResult.getCode())) {
            throw new ServiceException(stockResult.getCode(), stockResult.getMessage());
        }
        ItemStockVO itemStockVO = stockResult.getData();
        if (itemStockVO == null || CollectionUtils.isEmpty(itemStockVO.getStocks())) {
            target.setLocationStocks(new ArrayList<>());
            target.setLocationQuantity(BigDecimal.ZERO);
            return target;
        }
        List<ItemWithStockVO.LocationStock> locationStockList = itemStockVO.getStocks().stream()
                .filter(stock -> locationCode.equals(stock.getLocationCode()))
                .map(this::convertStock)
                .collect(Collectors.toList());
        target.setLocationStocks(locationStockList);

        // 将当前 location 的库存/价格信息组装到父类中的 skus 列表（按 sku 唯一）
        if (!CollectionUtils.isEmpty(target.getSkus())) {
            Map<String, ItemWithStockVO.LocationStock> stockBySku = locationStockList.stream()
                    .collect(Collectors.toMap(ItemWithStockVO.LocationStock::getSku, Function.identity(), (a, b) -> a));
            for (ItemVO.ItemSkuVO skuVO : target.getSkus()) {
                ItemWithStockVO.LocationStock ls = stockBySku.get(skuVO.getSku());
                if (ls != null) {
                    skuVO.setLocationCode(ls.getLocationCode());
                    skuVO.setLocationSku(ls.getLocationSku());
                    skuVO.setCurrencyCode(ls.getCurrencyCode());
                    skuVO.setPrice(ls.getPrice());
                    skuVO.setQuantity(ls.getQuantity());
                } else {
                    // 指定仓库没有该 SKU 的报价/库存，保留为空
                    skuVO.setLocationCode(null);
                    skuVO.setLocationSku(null);
                    skuVO.setCurrencyCode(null);
                    skuVO.setPrice(null);
                    skuVO.setQuantity(null);
                }
            }
        }

        target.setLocationQuantity(locationStockList.stream().map(ItemWithStockVO.LocationStock::getQuantity).reduce(BigDecimal.ZERO, BigDecimal::add));
        target.setMinPrice(locationStockList.stream()
                .map(ItemWithStockVO.LocationStock::getPrice)
                .filter(Objects::nonNull)
                .min(BigDecimal::compareTo)
                .orElse(null));
        target.setMaxPrice(locationStockList.stream()
                .map(ItemWithStockVO.LocationStock::getPrice)
                .filter(Objects::nonNull)
                .max(BigDecimal::compareTo)
                .orElse(null));
        return target;
    }

    private ItemWithStockVO.LocationStock convertStock(ListStockVO stockVO) {
        ItemWithStockVO.LocationStock locationStock = new ItemWithStockVO.LocationStock();
        locationStock.setSku(stockVO.getSku());
        locationStock.setLocationCode(stockVO.getLocationCode());
        locationStock.setPrice(stockVO.getPrice());
        locationStock.setQuantity(stockVO.getQuantity());
        locationStock.setCurrencyCode(stockVO.getCurrencyCode());
        locationStock.setLocationSku(stockVO.getLocationSku());
        locationStock.setSmc(stockVO.getSmc());
        return locationStock;
    }
}

