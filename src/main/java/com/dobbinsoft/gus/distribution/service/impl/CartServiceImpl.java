package com.dobbinsoft.gus.distribution.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.dobbinsoft.gus.common.model.vo.PageResult;
import com.dobbinsoft.gus.distribution.client.gus.product.ProductItemFeignClient;
import com.dobbinsoft.gus.distribution.client.gus.product.ProductStockFeignClient;
import com.dobbinsoft.gus.distribution.client.gus.product.model.*;
import com.dobbinsoft.gus.web.exception.BasicErrorCode;
import com.dobbinsoft.gus.web.vo.R;
import com.dobbinsoft.gus.distribution.data.dto.cart.AddCartItemDTO;
import com.dobbinsoft.gus.distribution.data.dto.session.FoSessionInfoDTO;
import com.dobbinsoft.gus.distribution.data.enums.CartReferType;
import com.dobbinsoft.gus.distribution.data.enums.StatusType;
import com.dobbinsoft.gus.distribution.data.exception.DistributionErrorCode;
import com.dobbinsoft.gus.web.exception.ServiceException;
import com.dobbinsoft.gus.distribution.data.po.CartItemCustomizationPO;
import com.dobbinsoft.gus.distribution.data.po.CartItemPO;
import com.dobbinsoft.gus.distribution.data.po.CartPO;
import com.dobbinsoft.gus.distribution.data.vo.cart.CartItemVO;
import com.dobbinsoft.gus.distribution.data.vo.cart.CartVO;
import com.dobbinsoft.gus.distribution.mapper.CartItemCustomizationMapper;
import com.dobbinsoft.gus.distribution.mapper.CartItemMapper;
import com.dobbinsoft.gus.distribution.mapper.CartMapper;
import com.dobbinsoft.gus.distribution.service.CartService;
import com.dobbinsoft.gus.distribution.service.biz.ConfigurationBizService;
import com.dobbinsoft.gus.distribution.utils.SessionUtils;
import com.dobbinsoft.gus.distribution.utils.UuidWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private CartItemMapper cartItemMapper;

    @Autowired
    private CartItemCustomizationMapper cartItemCustomizationMapper;

    @Autowired
    private ProductItemFeignClient productItemFeignClient;

    @Autowired
    private ProductStockFeignClient productStockFeignClient;

    @Autowired
    private ConfigurationBizService configurationBizService;


    private static final int MAX_CART_ITEMS = 100;

    @Override
    @Transactional
    public void addCartItem(AddCartItemDTO addCartItemDTO) {
        // 获取当前用户
        FoSessionInfoDTO sessionInfo = SessionUtils.getFoSession();

        // 验证商品信息
        R<ItemVO> itemResult = productItemFeignClient.getBySmc(addCartItemDTO.getSmc());
        if (!BasicErrorCode.SUCCESS.getCode().equals(itemResult.getCode())
                || itemResult.getData() == null
                || itemResult.getData().getStatus() == ItemStatus.DISABLED) {

            throw new ServiceException(DistributionErrorCode.ITEM_NOT_FOUND);
        }

        ItemVO itemVO = itemResult.getData();
        // 验证SKU是否存在
        boolean skuExists = itemVO.getSkus().stream()
                .anyMatch(sku -> sku.getSku().equals(addCartItemDTO.getSku()));
        if (!skuExists) {
            throw new ServiceException(DistributionErrorCode.INVALID_SKU);
        }

        // 获取库存价格
        R<ItemStockVO> itemStockVO = productStockFeignClient.itemStock(itemVO.getSmc());
        if (!BasicErrorCode.SUCCESS.getCode().equals(itemStockVO.getCode()) || itemStockVO.getData() == null || itemStockVO.getData().getStocks() == null) {
            throw new ServiceException(DistributionErrorCode.ITEM_NOT_FOUND);
        }

        // 获取EC库存
        List<ListStockVO> stocks = itemStockVO.getData().getStocks();
        ListStockVO stockVO = stocks.stream().filter(item -> item.getLocationCode().equals(configurationBizService.getConfiguration().getLocation().getCode()))
                .findFirst()
                .orElseThrow(() -> new ServiceException(DistributionErrorCode.ITEM_NOT_FOUND));

        // 获取或创建用户购物车
        CartPO cart = getOrCreateUserCart(sessionInfo.getUserId());

        // 检查购物车商品数量限制
        LambdaQueryWrapper<CartItemPO> countWrapper = new LambdaQueryWrapper<>();
        countWrapper.eq(CartItemPO::getCartId, cart.getId());
        long currentItemCount = cartItemMapper.selectCount(countWrapper);
        if (currentItemCount >= MAX_CART_ITEMS) {
            throw new ServiceException(DistributionErrorCode.CART_SMC_NUMBER_GREATER_THAN_MAX,
                    DistributionErrorCode.CART_SMC_NUMBER_GREATER_THAN_MAX.getMessage().formatted(MAX_CART_ITEMS));
        }

        // 生成客制化签名
        String customizationSignature = generateCustomizationSignature(addCartItemDTO.getCustomizations());

        // 查找是否已存在相同商品和客制化选项
        LambdaQueryWrapper<CartItemPO> existingWrapper = new LambdaQueryWrapper<>();
        existingWrapper.eq(CartItemPO::getCartId, cart.getId())
                .eq(CartItemPO::getSmc, addCartItemDTO.getSmc())
                .eq(CartItemPO::getSku, addCartItemDTO.getSku())
                .eq(CartItemPO::getCustomizationSignature, customizationSignature)
                .eq(CartItemPO::getStatus, StatusType.ENABLED.getCode());
        
        CartItemPO existingItem = cartItemMapper.selectOne(existingWrapper);

        if (existingItem != null) {
            // 更新数量
            existingItem.setQuantity(existingItem.getQuantity() + addCartItemDTO.getQuantity());
            cartItemMapper.updateById(existingItem);
        } else {
            // 创建新的购物车商品
            CartItemPO newItem = new CartItemPO();
            newItem.setCartId(cart.getId());
            newItem.setSmc(addCartItemDTO.getSmc());
            newItem.setSku(addCartItemDTO.getSku());
            newItem.setQuantity(addCartItemDTO.getQuantity());
            newItem.setEntryPrice(stockVO.getPrice());
            newItem.setStatus(StatusType.ENABLED.getCode());
            newItem.setCustomizationSignature(customizationSignature);
            
            cartItemMapper.insert(newItem);

            // 保存客制化选项
            if (!CollectionUtils.isEmpty(addCartItemDTO.getCustomizations())) {
                for (AddCartItemDTO.CartItemCustomizationDTO customization : addCartItemDTO.getCustomizations()) {
                    CartItemCustomizationPO customizationPO = new CartItemCustomizationPO();
                    customizationPO.setCartItemId(newItem.getId());
                    customizationPO.setTemplateId(customization.getTemplateId());
                    customizationPO.setOptionId(customization.getOptionId());
                    cartItemCustomizationMapper.insert(customizationPO);
                }
            }
        }

        // 更新购物车总数量
        updateCartQuantity(cart.getId());
    }

    @Override
    public CartVO getUserCart() {
        // 获取当前用户
        FoSessionInfoDTO sessionInfo = SessionUtils.getFoSession();

        // 获取用户购物车
        CartPO cart = getUserCartByUserId(sessionInfo.getUserId());
        if (cart == null) {
            // 返回空的购物车
            CartVO emptyCart = new CartVO();
            emptyCart.setItems(new ArrayList<>());
            emptyCart.setQuantity(0);
            return emptyCart;
        }

        // 获取购物车商品列表
        LambdaQueryWrapper<CartItemPO> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(CartItemPO::getCartId, cart.getId())
                .orderByAsc(CartItemPO::getStatus) // 有效商品在前
                .orderByDesc(CartItemPO::getCreatedTime); // 按创建时间倒序
        
        List<CartItemPO> cartItems = cartItemMapper.selectList(itemWrapper);

        // 批量查询商品信息
        Set<String> smcSet = cartItems.stream()
                .map(CartItemPO::getSmc)
                .collect(Collectors.toSet());
        
        Map<String, ItemVO> itemMap = new HashMap<>();
        if (!smcSet.isEmpty()) {
            ItemSearchDTO searchDTO = new ItemSearchDTO();
            searchDTO.setSmc(new ArrayList<>(smcSet));
            searchDTO.setPageSize(100);
            searchDTO.setPageNum(1);
            
            R<PageResult<ItemVO>> searchResult = productItemFeignClient.search(searchDTO);
            if (BasicErrorCode.SUCCESS.getCode().equals(searchResult.getCode()) && searchResult.getData() != null) {
                // 假设PageResult有getRecords()方法
                PageResult<ItemVO> data = searchResult.getData();
                data.getData().forEach(item -> {
                    itemMap.put(item.getSmc(), item);
                });
            }
        }

        // 验证商品状态并更新
        List<CartItemPO> validItems = new ArrayList<>();
        List<CartItemPO> invalidItems = new ArrayList<>();

        for (CartItemPO item : cartItems) {
            ItemVO itemVO = itemMap.get(item.getSmc());
            if (itemVO == null) {
                // 商品不存在，标记为失效
                if (item.getStatus().equals(StatusType.ENABLED.getCode())) {
                    item.setStatus(StatusType.DISABLED.getCode());
                    cartItemMapper.updateById(item);
                }
                invalidItems.add(item);
            } else {
                validItems.add(item);
            }
        }

        // 批量验证并更新客制化选项
        validateAndUpdateCustomizations(validItems, itemMap);

        // 合并相同签名的商品
        validItems = mergeSameSignatureItems(validItems);

        // 构建返回结果
        CartVO cartVO = new CartVO();
        cartVO.setId(cart.getId());
        cartVO.setReferType(cart.getReferType());
        cartVO.setReferId(cart.getReferId());
        cartVO.setQuantity(cart.getQuantity());
        cartVO.setCreateTime(cart.getCreatedTime());
        cartVO.setUpdateTime(cart.getModifiedTime());

        // 先添加有效商品，再添加失效商品
        List<CartItemVO> itemVOList = new ArrayList<>();
        itemVOList.addAll(convertToCartItemVOList(validItems, itemMap));
        itemVOList.addAll(convertToCartItemVOList(invalidItems, itemMap));
        
        cartVO.setItems(itemVOList);

        return cartVO;
    }

    @Override
    public Integer getCartItemCount() {
        // 获取当前用户
        FoSessionInfoDTO sessionInfo = SessionUtils.getFoSession();

        // 获取用户购物车
        CartPO cart = getUserCartByUserId(sessionInfo.getUserId());
        if (cart == null) {
            return 0;
        }

        // 直接返回主表中冗余的数量
        return cart.getQuantity();
    }

    @Override
    @Transactional
    public void removeCartItem(String cartItemId) {
        // 获取当前用户
        FoSessionInfoDTO sessionInfo = SessionUtils.getFoSession();

        // 验证购物车商品是否属于当前用户
        CartItemPO cartItem = cartItemMapper.selectById(cartItemId);
        if (cartItem == null) {
            throw new ServiceException(BasicErrorCode.NO_RESOURCE);
        }

        CartPO cart = cartMapper.selectById(cartItem.getCartId());
        validateCartOwnership(cart, sessionInfo.getUserId());

        // 删除客制化选项
        LambdaQueryWrapper<CartItemCustomizationPO> customizationWrapper = new LambdaQueryWrapper<>();
        customizationWrapper.eq(CartItemCustomizationPO::getCartItemId, cartItemId);
        cartItemCustomizationMapper.delete(customizationWrapper);

        // 删除购物车商品
        cartItemMapper.deleteById(cartItemId);

        // 更新购物车总数量
        updateCartQuantity(cart.getId());
    }

    @Override
    @Transactional
    public void updateCartItemQuantity(String cartItemId, Integer quantity) {
        if (quantity <= 0) {
            throw new ServiceException(DistributionErrorCode.CART_ITEM_QUANTITY_INVALID);
        }

        // 获取当前用户
        FoSessionInfoDTO sessionInfo = SessionUtils.getFoSession();

        // 验证购物车商品是否属于当前用户
        CartItemPO cartItem = cartItemMapper.selectById(cartItemId);
        if (cartItem == null) {
            throw new ServiceException(BasicErrorCode.NO_RESOURCE);
        }

        CartPO cart = cartMapper.selectById(cartItem.getCartId());
        validateCartOwnership(cart, sessionInfo.getUserId());

        // 更新数量
        cartItem.setQuantity(quantity);
        cartItemMapper.updateById(cartItem);

        // 更新购物车总数量
        updateCartQuantity(cart.getId());
    }

    /**
     * 获取或创建用户购物车
     */
    private CartPO getOrCreateUserCart(String userId) {
        CartPO cart = getUserCartByUserId(userId);
        if (cart == null) {
            cart = new CartPO();
            cart.setId(UuidWorker.nextId());
            cart.setReferType(CartReferType.USER.getCode());
            cart.setReferId(userId);
            cart.setQuantity(0);
            cartMapper.insert(cart);
        }
        return cart;
    }

    /**
     * 根据用户ID获取购物车
     */
    private CartPO getUserCartByUserId(String userId) {
        LambdaQueryWrapper<CartPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CartPO::getReferType, CartReferType.USER.getCode())
                .eq(CartPO::getReferId, userId);
        return cartMapper.selectOne(wrapper);
    }

    /**
     * 验证购物车是否属于当前用户
     */
    private void validateCartOwnership(CartPO cart, String userId) {
        if (cart == null || !CartReferType.USER.getCode().equals(cart.getReferType()) 
                || !userId.equals(cart.getReferId())) {
            throw new ServiceException(BasicErrorCode.NO_PERMISSION);
        }
    }

    /**
     * 生成客制化签名
     */
    private String generateCustomizationSignature(List<AddCartItemDTO.CartItemCustomizationDTO> customizations) {
        if (CollectionUtils.isEmpty(customizations)) {
            return "";
        }

        // 按模板ID和选项ID排序，确保相同选项生成相同签名
        List<String> signatures = customizations.stream()
                .sorted(Comparator.comparing(AddCartItemDTO.CartItemCustomizationDTO::getTemplateId)
                        .thenComparing(AddCartItemDTO.CartItemCustomizationDTO::getOptionId))
                .map(c -> c.getTemplateId() + ":" + c.getOptionId())
                .collect(Collectors.toList());
        return DigestUtils.md5DigestAsHex(String.join("|", signatures).getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 更新购物车总数量
     */
    private void updateCartQuantity(String cartId) {
        // 使用SQL聚合查询，避免加载所有数据到内存
        LambdaQueryWrapper<CartItemPO> countWrapper = new LambdaQueryWrapper<>();
        countWrapper.eq(CartItemPO::getCartId, cartId)
                .eq(CartItemPO::getStatus, StatusType.ENABLED.getCode())
                .select(CartItemPO::getQuantity);
        
        List<CartItemPO> items = cartItemMapper.selectList(countWrapper);
        long totalQuantity = items.stream()
                .mapToLong(CartItemPO::getQuantity)
                .sum();

        LambdaUpdateWrapper<CartPO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(CartPO::getId, cartId)
                .set(CartPO::getQuantity, (int) totalQuantity);
        cartMapper.update(null, updateWrapper);
    }

    /**
     * 验证并更新客制化选项
     */
    private void validateAndUpdateCustomizations(List<CartItemPO> cartItems, Map<String, ItemVO> itemMap) {
        if (CollectionUtils.isEmpty(cartItems)) {
            return;
        }
        
        // 批量查询所有客制化选项
        List<String> cartItemIds = cartItems.stream()
                .map(CartItemPO::getId)
                .collect(Collectors.toList());
        
        LambdaQueryWrapper<CartItemCustomizationPO> customizationWrapper = new LambdaQueryWrapper<>();
        customizationWrapper.in(CartItemCustomizationPO::getCartItemId, cartItemIds);
        List<CartItemCustomizationPO> allCustomizations = cartItemCustomizationMapper.selectList(customizationWrapper);
        
        // 按cartItemId分组
        Map<String, List<CartItemCustomizationPO>> customizationMap = allCustomizations.stream()
                .collect(Collectors.groupingBy(CartItemCustomizationPO::getCartItemId));
        
        List<String> invalidCartItemIds = new ArrayList<>();
        List<CartItemPO> itemsToUpdate = new ArrayList<>();
        
        for (CartItemPO cartItem : cartItems) {
            ItemVO itemVO = itemMap.get(cartItem.getSmc());
            if (itemVO == null) {
                continue;
            }
            
            List<CartItemCustomizationPO> currentCustomizations = customizationMap.get(cartItem.getId());
            if (CollectionUtils.isEmpty(currentCustomizations)) {
                continue;
            }

            // 验证客制化选项是否仍然有效
            boolean hasInvalidCustomization = false;
            for (CartItemCustomizationPO customization : currentCustomizations) {
                boolean isValid = itemVO.getCustomizationTemplates().stream()
                        .anyMatch(template -> template.getId().equals(customization.getTemplateId()) &&
                                template.getOptions().stream()
                                        .anyMatch(option -> option.getId().equals(customization.getOptionId())));
                
                if (!isValid) {
                    hasInvalidCustomization = true;
                    break;
                }
            }

            if (hasInvalidCustomization) {
                invalidCartItemIds.add(cartItem.getId());
                cartItem.setCustomizationSignature("");
                itemsToUpdate.add(cartItem);
            }
        }
        
        // 批量删除失效的客制化选项
        if (!invalidCartItemIds.isEmpty()) {
            LambdaQueryWrapper<CartItemCustomizationPO> deleteWrapper = new LambdaQueryWrapper<>();
            deleteWrapper.in(CartItemCustomizationPO::getCartItemId, invalidCartItemIds);
            cartItemCustomizationMapper.delete(deleteWrapper);
            
            // 批量更新购物车商品
            if (!itemsToUpdate.isEmpty()) {
                for (CartItemPO item : itemsToUpdate) {
                    cartItemMapper.updateById(item);
                }
            }
        }
    }

    /**
     * 合并相同签名的商品
     */
    private List<CartItemPO> mergeSameSignatureItems(List<CartItemPO> items) {
        Map<String, List<CartItemPO>> signatureGroups = items.stream()
                .collect(Collectors.groupingBy(CartItemPO::getCustomizationSignature));

        List<CartItemPO> mergedItems = new ArrayList<>();
        List<String> cartItemIdsToDelete = new ArrayList<>();
        List<String> cartItemIdsToUpdate = new ArrayList<>();
        
        for (List<CartItemPO> group : signatureGroups.values()) {
            if (group.size() == 1) {
                mergedItems.add(group.getFirst());
            } else {
                // 合并相同签名的商品
                CartItemPO mergedItem = group.getFirst();
                int totalQuantity = group.stream().mapToInt(CartItemPO::getQuantity).sum();
                mergedItem.setQuantity(totalQuantity);
                
                // 收集需要删除的商品ID
                for (int i = 1; i < group.size(); i++) {
                    cartItemIdsToDelete.add(group.get(i).getId());
                }
                
                cartItemIdsToUpdate.add(mergedItem.getId());
                mergedItems.add(mergedItem);
            }
        }
        
        // 批量删除客制化选项
        if (!cartItemIdsToDelete.isEmpty()) {
            LambdaQueryWrapper<CartItemCustomizationPO> customizationWrapper = new LambdaQueryWrapper<>();
            customizationWrapper.in(CartItemCustomizationPO::getCartItemId, cartItemIdsToDelete);
            cartItemCustomizationMapper.delete(customizationWrapper);
            
            // 批量删除购物车商品
            cartItemMapper.deleteByIds(cartItemIdsToDelete);
        }
        
        // 批量更新合并后的商品
        if (!cartItemIdsToUpdate.isEmpty()) {
            // 使用批量更新，减少数据库交互次数
            List<CartItemPO> itemsToUpdate = mergedItems.stream()
                    .filter(item -> cartItemIdsToUpdate.contains(item.getId()))
                    .toList();
            
            for (CartItemPO item : itemsToUpdate) {
                cartItemMapper.updateById(item);
            }
        }
        
        return mergedItems;
    }

    /**
     * 转换为CartItemVO列表
     */
    private List<CartItemVO> convertToCartItemVOList(List<CartItemPO> cartItems, Map<String, ItemVO> itemMap) {
        if (CollectionUtils.isEmpty(cartItems)) {
            return new ArrayList<>();
        }

        // 批量查询所有客制化选项，避免N+1查询问题
        List<String> cartItemIds = cartItems.stream()
                .map(CartItemPO::getId)
                .collect(Collectors.toList());
        
        Map<String, List<CartItemCustomizationPO>> customizationMap = new HashMap<>();
        if (!cartItemIds.isEmpty()) {
            LambdaQueryWrapper<CartItemCustomizationPO> customizationWrapper = new LambdaQueryWrapper<>();
            customizationWrapper.in(CartItemCustomizationPO::getCartItemId, cartItemIds);
            List<CartItemCustomizationPO> allCustomizations = cartItemCustomizationMapper.selectList(customizationWrapper);
            
            // 按cartItemId分组
            customizationMap = allCustomizations.stream()
                    .collect(Collectors.groupingBy(CartItemCustomizationPO::getCartItemId));
        }

        List<CartItemVO> result = new ArrayList<>();
        
        for (CartItemPO cartItem : cartItems) {
            CartItemVO cartItemVO = new CartItemVO();
            cartItemVO.setId(cartItem.getId());
            cartItemVO.setSmc(cartItem.getSmc());
            cartItemVO.setSku(cartItem.getSku());
            cartItemVO.setQuantity(cartItem.getQuantity());
            cartItemVO.setEntryPrice(cartItem.getEntryPrice());
            cartItemVO.setStatus(StatusType.getByCode(cartItem.getStatus()));
            cartItemVO.setCreateTime(cartItem.getCreatedTime());
            cartItemVO.setUpdateTime(cartItem.getModifiedTime());

            // 获取商品信息
            ItemVO itemVO = itemMap.get(cartItem.getSmc());
            if (itemVO != null) {
                cartItemVO.setItemInfo(itemVO);
            }

            // 获取客制化选项（从批量查询的结果中获取）
            List<CartItemCustomizationPO> customizations = customizationMap.get(cartItem.getId());
            if (!CollectionUtils.isEmpty(customizations)) {
                List<CartItemVO.CartItemCustomizationVO> customizationVOs = customizations.stream()
                        .map(customizationVO -> convertToCustomizationVO(cartItem, customizationVO, itemMap))
                        .collect(Collectors.toList());
                cartItemVO.setCustomizations(customizationVOs);
            }

            result.add(cartItemVO);
        }
        
        return result;
    }

    /**
     * 转换为客制化选项VO
     */
    private CartItemVO.CartItemCustomizationVO convertToCustomizationVO(CartItemPO cartItem, CartItemCustomizationPO customizationPO, Map<String, ItemVO> itemMap) {
        CartItemVO.CartItemCustomizationVO vo = new CartItemVO.CartItemCustomizationVO();
        vo.setTemplateId(customizationPO.getTemplateId());
        vo.setOptionId(customizationPO.getOptionId());
        String smc = cartItem.getSmc();
        ItemVO itemVO = itemMap.get(smc);
        if (itemVO != null && itemVO.getCustomizationTemplates() != null) {
            Optional<CustomizationTemplateDTO> templateOptional = itemVO.getCustomizationTemplates().stream().filter(template -> template.getId().equals(customizationPO.getTemplateId())).findFirst();
            if (templateOptional.isPresent()) {
                CustomizationTemplateDTO template = templateOptional.get();
                vo.setTemplateName(template.getName());
                Optional<CustomizationTemplateDTO.CustomizationOptionDTO> optionOptional = template.getOptions().stream().filter(option -> option.getId().equals(customizationPO.getOptionId())).findFirst();
                optionOptional.ifPresent(customizationOptionDTO -> vo.setOptionName(customizationOptionDTO.getName()));
            }
        }
        return vo;
    }
} 