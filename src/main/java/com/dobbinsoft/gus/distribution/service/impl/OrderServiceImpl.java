package com.dobbinsoft.gus.distribution.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dobbinsoft.gus.common.model.vo.PageResult;
import com.dobbinsoft.gus.distribution.client.gus.logistics.ExpressFeignClient;
import com.dobbinsoft.gus.distribution.client.gus.payment.TransactionFeignClient;
import com.dobbinsoft.gus.distribution.client.gus.payment.model.TransactionCreateDTO;
import com.dobbinsoft.gus.distribution.client.gus.payment.model.TransactionStatus;
import com.dobbinsoft.gus.distribution.client.gus.payment.model.TransactionUpdateEventDTO;
import com.dobbinsoft.gus.distribution.client.gus.product.ProductItemFeignClient;
import com.dobbinsoft.gus.distribution.client.gus.product.ProductStockFeignClient;
import com.dobbinsoft.gus.distribution.client.gus.product.model.BatchStockAdjustDTO;
import com.dobbinsoft.gus.distribution.client.gus.product.model.ItemSearchDTO;
import com.dobbinsoft.gus.distribution.client.gus.product.model.ItemVO;
import com.dobbinsoft.gus.distribution.data.dto.order.*;
import com.dobbinsoft.gus.distribution.data.dto.session.FoSessionInfoDTO;
import com.dobbinsoft.gus.distribution.data.enums.CurrencyCode;
import com.dobbinsoft.gus.distribution.data.enums.OrderStatusType;
import com.dobbinsoft.gus.distribution.data.enums.RefundStatusType;
import com.dobbinsoft.gus.distribution.data.enums.UserSrcType;
import com.dobbinsoft.gus.distribution.data.exception.DistributionErrorCode;
import com.dobbinsoft.gus.distribution.data.po.*;
import com.dobbinsoft.gus.distribution.data.vo.express.ExpressOrderVO;
import com.dobbinsoft.gus.distribution.data.vo.order.*;
import com.dobbinsoft.gus.distribution.mapper.*;
import com.dobbinsoft.gus.distribution.service.OrderService;
import com.dobbinsoft.gus.distribution.utils.SessionUtils;
import com.dobbinsoft.gus.distribution.utils.UuidWorker;
import com.dobbinsoft.gus.web.exception.BasicErrorCode;
import com.dobbinsoft.gus.web.exception.ServiceException;
import com.dobbinsoft.gus.web.vo.R;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final OrderRefundMapper orderRefundMapper;
    private final OrderRefundItemMapper orderRefundItemMapper;
    private final AddressMapper addressMapper;
    private final CartItemMapper cartItemMapper;
    private final UserSocialMapper userSocialMapper;
    private final ProductItemFeignClient productItemFeignClient;
    private final ProductStockFeignClient productStockFeignClient;
    private final ExpressFeignClient expressFeignClient;
    private final TransactionFeignClient transactionFeignClient;

    @Override
    public OrderPreviewVO preview(OrderSubmitDTO submitDTO) {
        // 获取用户信息
        FoSessionInfoDTO sessionInfo = SessionUtils.getFoSession();

        // 获取订单商品信息
        List<OrderItemInfo> orderItemInfos = getOrderItemInfos(submitDTO, sessionInfo.getUserId());
        
        // 计算商品总金额
        BigDecimal totalAmount = orderItemInfos.stream()
                .map(item -> item.getPrice().multiply(new BigDecimal(item.getQty())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        // 物流费用（暂时为0）
        BigDecimal logisticsAmount = BigDecimal.ZERO;

        // 计算支付金额
        BigDecimal payAmount = totalAmount.add(logisticsAmount);
        if (payAmount.compareTo(BigDecimal.ZERO) < 0) {
            payAmount = BigDecimal.ZERO;
        }

        OrderPreviewVO previewVO = new OrderPreviewVO();
        previewVO.setLogisticsAmount(logisticsAmount);
        previewVO.setTotalAmount(payAmount);

        return previewVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVO submit(OrderSubmitDTO submitDTO) {
        // 获取用户信息
        FoSessionInfoDTO sessionInfo = SessionUtils.getFoSession();

        // 获取订单商品信息
        List<OrderItemInfo> orderItemInfos = getOrderItemInfos(submitDTO, sessionInfo.getUserId());
        
        // 验证库存
        validateStock(orderItemInfos);

        // 计算商品总金额
        BigDecimal totalAmount = orderItemInfos.stream()
                .map(item -> item.getPrice().multiply(new BigDecimal(item.getQty())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 物流费用（暂时为0）
        BigDecimal logisticsAmount = BigDecimal.ZERO;

        // 计算支付金额
        BigDecimal payAmount = totalAmount.add(logisticsAmount);
        if (payAmount.compareTo(BigDecimal.ZERO) < 0) {
            payAmount = BigDecimal.ZERO;
        }

        // 获取收货地址
        AddressPO addressPO = addressMapper.selectById(submitDTO.getAddressId());
        if (addressPO == null || !addressPO.getUserId().equals(sessionInfo.getUserId())) {
            throw new ServiceException(BasicErrorCode.NO_RESOURCE);
        }

        // 创建订单
        OrderPO orderPO = new OrderPO();
        orderPO.setId(UuidWorker.nextId());
        orderPO.setOrderNo(generateOrderNo());
        orderPO.setUserId(sessionInfo.getUserId());
        orderPO.setStatus(OrderStatusType.UNPAY.getCode());
        orderPO.setAmount(totalAmount);
        orderPO.setPayAmount(payAmount);
        orderPO.setLogisticsEstimatedPrice(logisticsAmount);
        orderPO.setRemark(submitDTO.getRemark());

        // 设置收货地址
        OrderPO.Address orderAddress = new OrderPO.Address();
        orderAddress.setUserName(addressPO.getUserName());
        orderAddress.setTelNumber(addressPO.getTelNumber());
        orderAddress.setPostalCode(addressPO.getPostalCode());
        orderAddress.setProvinceName(addressPO.getProvinceName());
        orderAddress.setCityName(addressPO.getCityName());
        orderAddress.setCountyName(addressPO.getCountyName());
        orderPO.setAddress(orderAddress);

        // 保存订单
        orderMapper.insert(orderPO);

        // 创建订单商品
        List<OrderItemPO> orderItemPOs = new ArrayList<>();
        for (OrderItemInfo itemInfo : orderItemInfos) {
            OrderItemPO orderItemPO = new OrderItemPO();
            orderItemPO.setId(UuidWorker.nextId());
            orderItemPO.setOrderId(Long.valueOf(orderPO.getId()));
            orderItemPO.setItemName(itemInfo.getItemName());
            orderItemPO.setSkuName(itemInfo.getSkuName());
            orderItemPO.setSmc(itemInfo.getSmc());
            orderItemPO.setSku(itemInfo.getSku());
            orderItemPO.setCategoryId(itemInfo.getCategoryId());
            orderItemPO.setCategoryName(itemInfo.getCategoryName());
            orderItemPO.setUnit(itemInfo.getUnit());
            orderItemPO.setProductPic(itemInfo.getProductPic());
            orderItemPO.setProductPicSmall(itemInfo.getProductPicSmall());
            orderItemPO.setQty(itemInfo.getQty());
            orderItemPO.setPrice(itemInfo.getPrice());
            orderItemPO.setOriginalPrice(itemInfo.getOriginalPrice());
            orderItemPOs.add(orderItemPO);
        }

        // 批量保存订单商品
        for (OrderItemPO orderItemPO : orderItemPOs) {
            orderItemMapper.insert(orderItemPO);
        }

        // 如果是从购物车提交，删除购物车商品
        if (!CollectionUtils.isEmpty(submitDTO.getCartItemIds())) {
            cartItemMapper.delete(new LambdaQueryWrapper<CartItemPO>()
                    .in(CartItemPO::getId, submitDTO.getCartItemIds())
                    .eq(CartItemPO::getCartId, sessionInfo.getUserId()));
        }

        // 扣减库存（放在最后，如果失败方便回滚）
        try {
            deductStock(orderItemInfos);
        } catch (Exception e) {
            log.error("扣减库存失败", e);
            throw new ServiceException(BasicErrorCode.SYSTEM_ERROR);
        }

        // 转换为VO返回
        return convertToOrderVO(orderPO, orderItemPOs);
    }

    @Override
    public OrderDetailVO getByOrderNo(String orderNo) {
        FoSessionInfoDTO sessionInfo = SessionUtils.getFoSession();
        OrderPO orderPO = orderMapper.selectOne(
                new LambdaQueryWrapper<OrderPO>()
                        .eq(OrderPO::getOrderNo, orderNo));

        // 验证订单归属权限
        validateOrderOwnership(orderPO, sessionInfo, "无权限查看此订单");

        return convertToOrderDetailVO(orderPO);
    }

    @Override
    public OrderPrepayVO prepay(OrderPrepayDTO prepayDTO) {
        // 获取用户信息
        FoSessionInfoDTO sessionInfo = SessionUtils.getFoSession();

        // 查询订单信息
        OrderPO orderPO = orderMapper.selectOne(
                new LambdaQueryWrapper<OrderPO>()
                        .eq(OrderPO::getOrderNo, prepayDTO.getOrderNo()));

        // 验证订单归属权限
        validateOrderOwnership(orderPO, sessionInfo, "无权限操作此订单");

        // 检查订单状态是否可以支付
        if (!OrderStatusType.UNPAY.getCode().equals(orderPO.getStatus())) {
            throw new ServiceException(DistributionErrorCode.ORDER_STATUS_INVALID, "订单状态不允许支付");
        }

        // 查询订单商品信息
        List<OrderItemPO> orderItemPOs = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItemPO>()
                        .eq(OrderItemPO::getOrderId, orderPO.getId()));

        if (CollectionUtils.isEmpty(orderItemPOs)) {
            throw new ServiceException(BasicErrorCode.NO_RESOURCE, "订单商品不存在");
        }

        // 构建交易创建请求
        TransactionCreateDTO createDTO = new TransactionCreateDTO();
        createDTO.setProviderId(prepayDTO.getProviderId());
        createDTO.setOrderNo(orderPO.getOrderNo());
        createDTO.setCurrencyCode(ObjectUtils.firstNonNull(prepayDTO.getCurrencyCode(), CurrencyCode.CNY)); // 默认使用人民币
        createDTO.setAmount(orderPO.getPayAmount());
        if (sessionInfo.getSrc().equals(UserSrcType.DISTRIBUTION_WECHAT_WEB.name())) {
            UserSocialPO userSocialPO = userSocialMapper.selectOne(new LambdaQueryWrapper<UserSocialPO>()
                    .eq(UserSocialPO::getSrc, UserSrcType.DISTRIBUTION_WECHAT_WEB.name())
                    .eq(UserSocialPO::getUserId, orderPO.getUserId()));
            if (userSocialPO == null) {
                throw new ServiceException(BasicErrorCode.NO_RESOURCE);
            }
            createDTO.setOpenId(userSocialPO.getSocialId());
        }
        // 构建商品列表
        List<TransactionCreateDTO.Item> items = orderItemPOs.stream().map(itemPO -> {
            TransactionCreateDTO.Item item = new TransactionCreateDTO.Item();
            item.setSku(itemPO.getSku());
            item.setName(itemPO.getItemName());
            item.setQuantity(new BigDecimal(itemPO.getQty()));
            item.setPrice(itemPO.getPrice());
            return item;
        }).collect(Collectors.toList());

        createDTO.setItems(items);

        // 调用支付服务创建预支付订单
        R<Object> response = transactionFeignClient.prepay(createDTO);
        if (!BasicErrorCode.SUCCESS.getCode().equals(response.getCode())) {
            throw new ServiceException(BasicErrorCode.SYSTEM_ERROR, response.getMessage());
        }

        // 构建返回结果
        OrderPrepayVO prepayVO = new OrderPrepayVO();
        prepayVO.setOrderNo(orderPO.getOrderNo());
        prepayVO.setPaymentParams(response.getData());
        prepayVO.setPayAmount(orderPO.getPayAmount());
        prepayVO.setProviderId(prepayDTO.getProviderId());

        return prepayVO;
    }


    /**
     * 获取订单商品信息
     */
    private List<OrderItemInfo> getOrderItemInfos(OrderSubmitDTO submitDTO, String userId) {
        List<OrderItemInfo> orderItemInfos = new ArrayList<>();

        // 处理直接提交的商品
        if (!CollectionUtils.isEmpty(submitDTO.getOrderItems())) {
            // 获取SKU列表
            List<String> skuIds = submitDTO.getOrderItems().stream()
                    .map(OrderSubmitDTO.OrderItem::getSkuId)
                    .collect(Collectors.toList());

            // 查询商品信息
            ItemSearchDTO searchDTO = new ItemSearchDTO();
            searchDTO.setSku(skuIds);
            searchDTO.setPageSize(100); // 单次最多100个SKU
            searchDTO.setPageNum(1);

            R<PageResult<ItemVO>> response = productItemFeignClient.search(searchDTO);
            if (!BasicErrorCode.SUCCESS.getCode().equals(response.getCode()) || response.getData() == null) {
                throw new ServiceException(BasicErrorCode.SYSTEM_ERROR, response.getMessage());
            }

            Map<String, ItemVO> itemMap = response.getData().getData().stream()
                    .collect(Collectors.toMap(ItemVO::getSmc, item -> item));

            // 构建订单商品信息
            for (OrderSubmitDTO.OrderItem orderItem : submitDTO.getOrderItems()) {
                ItemVO itemVO = itemMap.values().stream()
                        .filter(item -> item.getSkus().stream()
                                .anyMatch(sku -> sku.getSku().equals(orderItem.getSkuId())))
                        .findFirst()
                        .orElse(null);

                if (itemVO == null) {
                    throw new ServiceException(DistributionErrorCode.ITEM_NOT_FOUND);
                }

                // 获取SKU信息
                ItemVO.ItemSkuVO skuVO = itemVO.getSkus().stream()
                        .filter(sku -> sku.getSku().equals(orderItem.getSkuId()))
                        .findFirst()
                        .orElse(null);

                if (skuVO == null) {
                    throw new ServiceException(DistributionErrorCode.INVALID_SKU);
                }

                OrderItemInfo itemInfo = new OrderItemInfo();
                itemInfo.setSmc(itemVO.getSmc());
                itemInfo.setSku(orderItem.getSkuId());
                itemInfo.setQty(orderItem.getQty());
                itemInfo.setStockable(itemVO.getStockable());
                
                // 设置商品信息（这里简化处理，实际应该从商品详情中获取价格等信息）
                itemInfo.setItemName(itemVO.getSmc());
                itemInfo.setSkuName(orderItem.getSkuId());
                itemInfo.setPrice(BigDecimal.valueOf(100)); // 临时价格，实际应该从商品详情获取
                itemInfo.setOriginalPrice(BigDecimal.valueOf(120)); // 临时原价
                
                if (!CollectionUtils.isEmpty(itemVO.getCategories())) {
                    itemInfo.setCategoryId(itemVO.getCategories().get(0).getId().toString());
                    itemInfo.setCategoryName(itemVO.getCategories().get(0).getName());
                }
                
                if (itemVO.getUnitGroup() != null) {
                    itemInfo.setUnit(itemVO.getUnitGroup().getName());
                }
                
                if (!CollectionUtils.isEmpty(itemVO.getImages())) {
                    itemInfo.setProductPic(itemVO.getImages().get(0));
                    itemInfo.setProductPicSmall(itemVO.getImages().get(0));
                }

                orderItemInfos.add(itemInfo);
            }
        }

        // 处理购物车商品
        if (!CollectionUtils.isEmpty(submitDTO.getCartItemIds())) {
            List<CartItemPO> cartItems = cartItemMapper.selectList(
                    new LambdaQueryWrapper<CartItemPO>()
                            .in(CartItemPO::getId, submitDTO.getCartItemIds())
                            .eq(CartItemPO::getCartId, userId));

            if (cartItems.size() != submitDTO.getCartItemIds().size()) {
                throw new ServiceException(BasicErrorCode.NO_RESOURCE);
            }

            // 获取SKU列表
            List<String> skuIds = cartItems.stream()
                    .map(CartItemPO::getSku)
                    .collect(Collectors.toList());

            // 查询商品信息
            ItemSearchDTO searchDTO = new ItemSearchDTO();
            searchDTO.setSku(skuIds);
            searchDTO.setPageSize(100);
            searchDTO.setPageNum(1);

            R<PageResult<ItemVO>> response = productItemFeignClient.search(searchDTO);
            if (!BasicErrorCode.SUCCESS.getCode().equals(response.getCode()) || response.getData() == null) {
                throw new ServiceException(BasicErrorCode.SYSTEM_ERROR, response.getMessage());
            }

            Map<String, ItemVO> itemMap = response.getData().getData().stream()
                    .collect(Collectors.toMap(ItemVO::getSmc, item -> item));

            // 构建订单商品信息
            for (CartItemPO cartItem : cartItems) {
                ItemVO itemVO = itemMap.values().stream()
                        .filter(item -> item.getSkus().stream()
                                .anyMatch(sku -> sku.getSku().equals(cartItem.getSku())))
                        .findFirst()
                        .orElse(null);

                if (itemVO == null) {
                    throw new ServiceException(DistributionErrorCode.ITEM_NOT_FOUND);
                }

                OrderItemInfo itemInfo = new OrderItemInfo();
                itemInfo.setSmc(itemVO.getSmc());
                itemInfo.setSku(cartItem.getSku());
                itemInfo.setQty(cartItem.getQuantity());
                itemInfo.setStockable(itemVO.getStockable());
                
                // 设置商品信息
                itemInfo.setItemName(itemVO.getSmc());
                itemInfo.setSkuName(cartItem.getSku());
                itemInfo.setPrice(cartItem.getEntryPrice());
                itemInfo.setOriginalPrice(cartItem.getEntryPrice());
                
                if (!CollectionUtils.isEmpty(itemVO.getCategories())) {
                    itemInfo.setCategoryId(itemVO.getCategories().get(0).getId().toString());
                    itemInfo.setCategoryName(itemVO.getCategories().get(0).getName());
                }
                
                if (itemVO.getUnitGroup() != null) {
                    itemInfo.setUnit(itemVO.getUnitGroup().getName());
                }
                
                if (!CollectionUtils.isEmpty(itemVO.getImages())) {
                    itemInfo.setProductPic(itemVO.getImages().get(0));
                    itemInfo.setProductPicSmall(itemVO.getImages().get(0));
                }

                orderItemInfos.add(itemInfo);
            }
        }

        // 验证商品数量
        if (CollectionUtils.isEmpty(orderItemInfos)) {
            throw new ServiceException(DistributionErrorCode.ORDER_ITEMS_EMPTY);
        }

        // 验证SKU数量不超过100
        int totalSkuCount = orderItemInfos.stream().mapToInt(OrderItemInfo::getQty).sum();
        if (totalSkuCount > 100) {
            throw new ServiceException(DistributionErrorCode.ORDER_SKU_COUNT_EXCEEDED);
        }

        // 验证库存类型不能混合
        boolean hasStockable = orderItemInfos.stream().anyMatch(OrderItemInfo::getStockable);
        boolean hasNonStockable = orderItemInfos.stream().anyMatch(item -> !item.getStockable());
        if (hasStockable && hasNonStockable) {
            throw new ServiceException(DistributionErrorCode.ORDER_MIXED_STOCK_TYPES);
        }

        return orderItemInfos;
    }

    /**
     * 验证库存
     */
    private void validateStock(List<OrderItemInfo> orderItemInfos) {
        for (OrderItemInfo itemInfo : orderItemInfos) {
            if (itemInfo.getStockable()) {
                // 对于库存商品，需要验证库存是否充足
                // 这里简化处理，实际应该调用库存服务验证
                log.info("验证库存: SKU={}, 数量={}", itemInfo.getSku(), itemInfo.getQty());
            }
        }
    }

    /**
     * 扣减库存
     */
    private void deductStock(List<OrderItemInfo> orderItemInfos) {
        List<BatchStockAdjustDTO.ItemDTO> stockAdjustItems = new ArrayList<>();
        
        for (OrderItemInfo itemInfo : orderItemInfos) {
            if (itemInfo.getStockable()) {
                BatchStockAdjustDTO.ItemDTO itemDTO = new BatchStockAdjustDTO.ItemDTO();
                itemDTO.setLocationSku(itemInfo.getSku()); // 这里简化处理，实际应该包含仓库信息
                itemDTO.setOperation(BatchStockAdjustDTO.Operation.SUBTRACT);
                itemDTO.setQuantity(new BigDecimal(itemInfo.getQty()));
                stockAdjustItems.add(itemDTO);
            }
        }

        if (!CollectionUtils.isEmpty(stockAdjustItems)) {
            BatchStockAdjustDTO batchStockAdjustDTO = new BatchStockAdjustDTO();
            batchStockAdjustDTO.setItems(stockAdjustItems);

            R<Void> response = productStockFeignClient.adjustStock(batchStockAdjustDTO);
            if (!BasicErrorCode.SUCCESS.getCode().equals(response.getCode())) {
                throw new ServiceException(BasicErrorCode.SYSTEM_ERROR, response.getMessage());
            }
        }
    }

    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        return "O" + System.currentTimeMillis() + String.format("%04d", new Random().nextInt(10000));
    }

    /**
     * 转换为OrderVO
     */
    private OrderVO convertToOrderVO(OrderPO orderPO, List<OrderItemPO> orderItemPOs) {
        OrderVO orderVO = new OrderVO();
        orderVO.setId(orderPO.getId());
        orderVO.setOrderNo(orderPO.getOrderNo());
        orderVO.setStatus(orderPO.getStatus());
        orderVO.setStatusMsg(OrderStatusType.getStatusByCode(orderPO.getStatus()).getMsg());
        orderVO.setPayAmount(orderPO.getPayAmount());
        orderVO.setAmount(orderPO.getAmount());
        orderVO.setRemark(orderPO.getRemark());
        orderVO.setPayMethod(orderPO.getPayMethod());
        orderVO.setPayNo(orderPO.getPayNo());
        orderVO.setCreateTime(ZonedDateTime.now()); // 临时设置，实际应该从orderPO获取
        orderVO.setDeliveryTime(orderPO.getExpressTime());
        orderVO.setConfirmTime(orderPO.getConfirmTime());

        // 设置地址信息
        if (orderPO.getAddress() != null) {
            OrderVO.OrderAddressVO addressVO = new OrderVO.OrderAddressVO();
            addressVO.setUserName(orderPO.getAddress().getUserName());
            addressVO.setTelNumber(orderPO.getAddress().getTelNumber());
            addressVO.setPostalCode(orderPO.getAddress().getPostalCode());
            addressVO.setProvinceName(orderPO.getAddress().getProvinceName());
            addressVO.setCityName(orderPO.getAddress().getCityName());
            addressVO.setCountyName(orderPO.getAddress().getCountyName());
            orderVO.setAddress(addressVO);
        }

        // 设置订单商品
        List<OrderVO.OrderItemVO> orderItemVOs = orderItemPOs.stream().map(itemPO -> {
            OrderVO.OrderItemVO itemVO = new OrderVO.OrderItemVO();
            itemVO.setId(itemPO.getId());
            itemVO.setItemName(itemPO.getItemName());
            itemVO.setSkuName(itemPO.getSkuName());
            itemVO.setSmc(itemPO.getSmc());
            itemVO.setSku(itemPO.getSku());
            itemVO.setCategoryId(itemPO.getCategoryId());
            itemVO.setCategoryName(itemPO.getCategoryName());
            itemVO.setUnit(itemPO.getUnit());
            itemVO.setProductPic(itemPO.getProductPic());
            itemVO.setProductPicSmall(itemPO.getProductPicSmall());
            itemVO.setQty(itemPO.getQty());
            itemVO.setPrice(itemPO.getPrice());
            itemVO.setOriginalPrice(itemPO.getOriginalPrice());
            itemVO.setRemark(itemPO.getRemark());
            return itemVO;
        }).collect(Collectors.toList());

        orderVO.setOrderItems(orderItemVOs);

        return orderVO;
    }

    // ========== 后台管理接口实现 ==========

    @Override
    public PageResult<OrderListVO> page(OrderSearchDTO searchDTO) {
        Page<OrderPO> page = new Page<>(searchDTO.getPageNum(), searchDTO.getPageSize());
        
        LambdaQueryWrapper<OrderPO> queryWrapper = new LambdaQueryWrapper<>();
        
        // 订单号查询
        if (StringUtils.hasText(searchDTO.getOrderNo())) {
            queryWrapper.like(OrderPO::getOrderNo, searchDTO.getOrderNo());
        }

        // 订单状态查询
        if (searchDTO.getStatus() != null) {
            queryWrapper.eq(OrderPO::getStatus, searchDTO.getStatus());
        }

        // 物流公司查询
        if (StringUtils.hasText(searchDTO.getLogisticsCompany())) {
            queryWrapper.like(OrderPO::getLogisticsCompany, searchDTO.getLogisticsCompany());
        }
        
        // 物流单号查询
        if (StringUtils.hasText(searchDTO.getLogisticsNo())) {
            queryWrapper.like(OrderPO::getLogisticsNo, searchDTO.getLogisticsNo());
        }
        
        // 创建时间范围查询
        if (searchDTO.getPayTimeStart() != null) {
            queryWrapper.ge(OrderPO::getPayTime, searchDTO.getPayTimeStart());
        }
        if (searchDTO.getPayTimeEnd() != null) {
            queryWrapper.le(OrderPO::getPayTime, searchDTO.getPayTimeEnd());
        }
        
        // 发货时间范围查询
        if (searchDTO.getDeliveryTimeStart() != null) {
            queryWrapper.ge(OrderPO::getExpressTime, searchDTO.getDeliveryTimeStart());
        }
        if (searchDTO.getDeliveryTimeEnd() != null) {
            queryWrapper.le(OrderPO::getExpressTime, searchDTO.getDeliveryTimeEnd());
        }
        
        // 按创建时间倒序排列
        queryWrapper.orderByDesc(OrderPO::getId);
        
        Page<OrderPO> resultPage = orderMapper.selectPage(page, queryWrapper);
        
        List<OrderListVO> voList = new ArrayList<>();
        for (OrderPO po : resultPage.getRecords()) {
            OrderListVO vo = convertToOrderListVO(po);
            voList.add(vo);
        }

        return PageResult.<OrderListVO>builder()
                .totalCount(resultPage.getTotal())
                .totalPages(resultPage.getPages())
                .pageNumber((int) resultPage.getCurrent())
                .pageSize((int) resultPage.getSize())
                .hasMore(resultPage.hasNext())
                .data(voList)
                .build();
    }

    @Override
    public OrderDetailVO getDetailByOrderNo(String orderNo) {
        OrderPO orderPO = orderMapper.selectOne(
            new LambdaQueryWrapper<OrderPO>()
                .eq(OrderPO::getOrderNo, orderNo)
        );
        if (orderPO == null) {
            throw new ServiceException(BasicErrorCode.NO_RESOURCE);
        }
        
        return convertToOrderDetailVO(orderPO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void express(String orderNo, OrderExpressDTO expressDTO) {
        OrderPO orderPO = orderMapper.selectOne(
            new LambdaQueryWrapper<OrderPO>()
                .eq(OrderPO::getOrderNo, orderNo)
        );
        if (orderPO == null) {
            throw new ServiceException(BasicErrorCode.NO_RESOURCE);
        }
        
        // 检查订单状态是否可以发货
        if (!OrderStatusType.WAIT_STOCK.getCode().equals(orderPO.getStatus())) {
            throw new ServiceException(DistributionErrorCode.ORDER_STATUS_INVALID, "订单状态不允许发货");
        }

        // 查询物流单是否已存在
        String mobile = orderPO.getAddress().getTelNumber();
        R<ExpressOrderVO> expressResult = expressFeignClient.get(
                orderNo,
                expressDTO.getLogisticsCompanyCode(),
                expressDTO.getLogisticsNo(),
                mobile
        );

        if (!BasicErrorCode.SUCCESS.getCode().equals(expressResult.getCode())) {
            throw new ServiceException(expressResult.getCode(), expressResult.getMessage());
        }

        ExpressOrderVO data = expressResult.getData();
        orderPO.setLogisticsCompany(data.getLpName());
        // 更新订单信息
        orderPO.setStatus(OrderStatusType.WAIT_CONFIRM.getCode());
        orderPO.setLogisticsCompanyCode(expressDTO.getLogisticsCompanyCode().name());
        orderPO.setLogisticsNo(expressDTO.getLogisticsNo());
        orderPO.setExpressTime(ZonedDateTime.now());

        orderMapper.updateById(orderPO);
        log.info("订单发货成功: orderNo={}, logisticsNo={}", 
            orderNo, expressDTO.getLogisticsNo());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmReceipt(String orderNo) {
        // 获取用户信息
        FoSessionInfoDTO sessionInfo = SessionUtils.getFoSession();
        
        OrderPO orderPO = orderMapper.selectOne(
            new LambdaQueryWrapper<OrderPO>()
                .eq(OrderPO::getOrderNo, orderNo)
        );

        // 验证订单归属权限
        validateOrderOwnership(orderPO, sessionInfo, "无权限操作此订单");
        
        // 检查订单状态是否可以确认收货
        if (!OrderStatusType.WAIT_CONFIRM.getCode().equals(orderPO.getStatus())) {
            throw new ServiceException(DistributionErrorCode.ORDER_STATUS_INVALID, "订单状态不允许确认收货");
        }
        
        // 更新订单状态
        orderPO.setStatus(OrderStatusType.WAIT_COMMENT.getCode());
        orderPO.setConfirmTime(ZonedDateTime.now());
        
        orderMapper.updateById(orderPO);
        
        log.info("订单确认收货成功: orderNo={}", orderNo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approveRefund(String orderNo, OrderRefundApprovalDTO approvalDTO) {
        // 这里需要OrderRefundMapper，暂时简化处理
        // 实际应该查询退款记录，更新退款状态等
        log.info("退款审核: orderNo={}, refundId={}, approved={}, remark={}", 
            orderNo, approvalDTO.getRefundId(), approvalDTO.getApproved(), approvalDTO.getRemark());
        
        // TODO: 实现退款审核逻辑
        // 1. 查询退款记录
        // 2. 检查退款状态
        // 3. 更新退款状态
        // 4. 如果同意退款，处理退款逻辑
        // 5. 更新订单状态
    }

    // ========== 前台退款接口实现 ==========

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderRefundVO applyRefund(OrderRefundApplyDTO applyDTO) {
        // 获取用户信息
        FoSessionInfoDTO sessionInfo = SessionUtils.getFoSession();

        // 查询订单信息
        OrderPO orderPO = orderMapper.selectOne(
                new LambdaQueryWrapper<OrderPO>()
                        .eq(OrderPO::getOrderNo, applyDTO.getOrderNo()));

        // 验证订单归属权限
        validateOrderOwnership(orderPO, sessionInfo, "无权限操作此订单");

        // 检查订单状态是否可以退款
        if (!OrderStatusType.refundable(orderPO.getStatus())) {
            throw new ServiceException(DistributionErrorCode.ORDER_STATUS_INVALID, "订单状态不允许退款");
        }

        // 验证退款项
        if (applyDTO.getOrderRefundItems() == null || applyDTO.getOrderRefundItems().isEmpty()) {
            throw new ServiceException(BasicErrorCode.PARAMERROR);
        }

        // 计算总退款金额
        BigDecimal totalRefundAmount = BigDecimal.ZERO;
        
        // 创建退款记录
        OrderRefundPO refundPO = new OrderRefundPO();
        refundPO.setId(UuidWorker.nextId());
        refundPO.setRefundNo(generateRefundNo());
        refundPO.setOrderId(orderPO.getId());
        refundPO.setOrderNo(orderPO.getOrderNo());
        refundPO.setUserId(sessionInfo.getUserId());
        refundPO.setStatus(RefundStatusType.PENDING.getCode());
        refundPO.setReason(applyDTO.getReason());
        refundPO.setRemark(applyDTO.getRemark());
        
        // 处理凭证图片
        if (applyDTO.getEvidenceImages() != null && applyDTO.getEvidenceImages().length > 0) {
            refundPO.setEvidenceImages(String.join(",", applyDTO.getEvidenceImages()));
        }

        // 处理退款项
        List<OrderRefundItemPO> refundItemPOs = new ArrayList<>();
        for (OrderRefundItemDTO itemDTO : applyDTO.getOrderRefundItems()) {
            // 查询订单商品项
            OrderItemPO orderItemPO = orderItemMapper.selectOne(
                    new LambdaQueryWrapper<OrderItemPO>()
                            .eq(OrderItemPO::getId, Long.valueOf(itemDTO.getOrderItemId()))
                            .eq(OrderItemPO::getOrderId, Long.valueOf(orderPO.getId())));
            if (orderItemPO == null) {
                throw new ServiceException(BasicErrorCode.NO_RESOURCE, "订单商品项不存在: " + itemDTO.getOrderItemId());
            }

            // 查询已申请退款的数量（包括待处理、已批准和处理中的退款）
            List<OrderRefundItemPO> existingRefundItems = orderRefundItemMapper.selectList(
                    new LambdaQueryWrapper<OrderRefundItemPO>()
                            .eq(OrderRefundItemPO::getOrderItemId, itemDTO.getOrderItemId()));
            
            // 计算已申请退款的数量
            int alreadyRefundQty = 0;
            if (existingRefundItems != null && !existingRefundItems.isEmpty()) {
                for (OrderRefundItemPO refundItem : existingRefundItems) {
                    // 查询退款单状态
                    OrderRefundPO refundItemPO = orderRefundMapper.selectById(refundItem.getRefundId());
                    if (refundItemPO != null && (
                            RefundStatusType.PENDING.getCode().equals(refundItemPO.getStatus()) ||
                            RefundStatusType.APPROVED.getCode().equals(refundItemPO.getStatus()) ||
                            RefundStatusType.PROCESSING.getCode().equals(refundItemPO.getStatus())
                        )) {
                        alreadyRefundQty += refundItem.getRefundQty();
                    }
                }
            }
            
            // 验证退款数量
            int totalRefundQty = alreadyRefundQty + itemDTO.getRefundQty();
            if (totalRefundQty > orderItemPO.getQty()) {
                throw new ServiceException(DistributionErrorCode.REFUND_AMOUNT_EXCEEDED,
                        "退款数量超过购买数量: 已申请" + alreadyRefundQty + ", 本次申请" + itemDTO.getRefundQty() + ", 商品总数量" + orderItemPO.getQty());
            }

            // 验证退款数量是否大于0
            if (itemDTO.getRefundQty() <= 0) {
                throw new ServiceException(DistributionErrorCode.REFUND_AMOUNT_EXCEEDED,
                        "退款数量必须大于0: " + itemDTO.getRefundQty());
            }
            // 注意：已在前面验证了退款总数量是否超过购买数量

            // 计算退款金额 - 根据SKU价格和退款数量计算
            BigDecimal itemRefundAmount = orderItemPO.getPrice().multiply(new BigDecimal(itemDTO.getRefundQty()));

            // 创建退款项记录
            OrderRefundItemPO refundItemPO = new OrderRefundItemPO();
            refundItemPO.setId(UuidWorker.nextId());
            refundItemPO.setRefundId(refundPO.getId());
            refundItemPO.setOrderItemId(itemDTO.getOrderItemId());
            refundItemPO.setItemName(orderItemPO.getItemName());
            refundItemPO.setSkuName(orderItemPO.getSkuName());
            refundItemPO.setSmc(orderItemPO.getSmc());
            refundItemPO.setSku(orderItemPO.getSku());
            refundItemPO.setRefundQty(itemDTO.getRefundQty());
            refundItemPO.setPrice(orderItemPO.getPrice());
            refundItemPO.setRefundAmount(itemRefundAmount);
            refundItemPO.setRemark(itemDTO.getRemark());
            
            refundItemPOs.add(refundItemPO);
            totalRefundAmount = totalRefundAmount.add(itemRefundAmount);
        }

        // 设置总退款金额
        refundPO.setRefundAmount(totalRefundAmount);
        
        // 保存退款记录
        orderRefundMapper.insert(refundPO);
        
        // 保存退款项记录
        for (OrderRefundItemPO refundItemPO : refundItemPOs) {
            orderRefundItemMapper.insert(refundItemPO);
        }

        // 更新订单状态为退款中
        orderPO.setStatus(OrderStatusType.REFUNDING.getCode());
        orderMapper.updateById(orderPO);

        log.info("用户申请退款成功: userId={}, orderNo={}, refundNo={}, totalAmount={}", 
                sessionInfo.getUserId(), applyDTO.getOrderNo(), refundPO.getRefundNo(), totalRefundAmount);

        return convertToOrderRefundVO(refundPO, refundItemPOs);
    }

    @Override
    public List<OrderRefundVO> getUserRefunds(String orderNo) {
        // 获取用户信息
        FoSessionInfoDTO sessionInfo = SessionUtils.getFoSession();

        LambdaQueryWrapper<OrderRefundPO> queryWrapper = new LambdaQueryWrapper<OrderRefundPO>()
                .eq(OrderRefundPO::getUserId, sessionInfo.getUserId())
                .orderByDesc(OrderRefundPO::getCreatedTime);

        if (StringUtils.hasText(orderNo)) {
            queryWrapper.eq(OrderRefundPO::getOrderNo, orderNo);
        }

        List<OrderRefundPO> refundPOs = orderRefundMapper.selectList(queryWrapper);
        return refundPOs.stream()
                .map(this::convertToOrderRefundVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelRefund(String refundId) {
        // 获取用户信息
        FoSessionInfoDTO sessionInfo = SessionUtils.getFoSession();

        // 查询退款记录
        OrderRefundPO refundPO = orderRefundMapper.selectById(refundId);

        // 验证退款归属权限
        validateRefundOwnership(refundPO, sessionInfo, "无权限操作此退款");

        // 检查退款状态是否可以取消
        if (!RefundStatusType.canCancel(refundPO.getStatus())) {
            throw new ServiceException(DistributionErrorCode.REFUND_STATUS_INVALID, "退款状态不允许取消");
        }

        // 更新退款状态
        refundPO.setStatus(RefundStatusType.CANCELLED.getCode());
        orderRefundMapper.updateById(refundPO);

        // 如果订单状态是退款中，需要恢复原状态
        OrderPO orderPO = orderMapper.selectById(Long.valueOf(refundPO.getOrderId()));
        if (orderPO != null && OrderStatusType.REFUNDING.getCode().equals(orderPO.getStatus())) {
            // 检查是否还有其他待处理的退款
            long pendingRefundCount = orderRefundMapper.selectCount(
                    new LambdaQueryWrapper<OrderRefundPO>()
                            .eq(OrderRefundPO::getOrderId, refundPO.getOrderId())
                            .in(OrderRefundPO::getStatus,
                                RefundStatusType.PENDING.getCode(),
                                RefundStatusType.APPROVED.getCode(),
                                RefundStatusType.PROCESSING.getCode()));

            if (pendingRefundCount == 0) {
                // 没有其他待处理的退款，恢复订单状态
                orderPO.setStatus(OrderStatusType.WAIT_STOCK.getCode()); // 恢复到待出库状态
                orderMapper.updateById(orderPO);
            }
        }

        log.info("用户取消退款成功: userId={}, refundId={}", sessionInfo.getUserId(), refundId);
    }

    /**
     * 生成退款单号
     */
    private String generateRefundNo() {
        return "R" + System.currentTimeMillis() + String.format("%04d", new Random().nextInt(10000));
    }

    /**
     * 转换为OrderRefundVO
     */
    private OrderRefundVO convertToOrderRefundVO(OrderRefundPO refundPO) {
        return convertToOrderRefundVO(refundPO, null);
    }
    
    /**
     * 转换为OrderRefundVO（包含退款项）
     */
    private OrderRefundVO convertToOrderRefundVO(OrderRefundPO refundPO, List<OrderRefundItemPO> refundItemPOs) {
        OrderRefundVO refundVO = new OrderRefundVO();
        refundVO.setId(refundPO.getId());
        refundVO.setRefundNo(refundPO.getRefundNo());
        refundVO.setOrderId(refundPO.getOrderId());
        refundVO.setOrderNo(refundPO.getOrderNo());
        refundVO.setUserId(refundPO.getUserId());
        refundVO.setStatus(refundPO.getStatus());
        refundVO.setStatusMsg(RefundStatusType.getStatusByCode(refundPO.getStatus()).getMsg());
        refundVO.setRefundAmount(refundPO.getRefundAmount());
        refundVO.setReason(refundPO.getReason());
        refundVO.setRemark(refundPO.getRemark());
        refundVO.setInnerRemark(refundPO.getInnerRemark());
        refundVO.setApproverId(refundPO.getApproverId());
        refundVO.setCreateTime(ZonedDateTime.now()); // 临时设置，实际应该从refundPO获取
        refundVO.setApprovalTime(refundPO.getApprovalTime());
        refundVO.setProcessTime(refundPO.getProcessTime());
        refundVO.setCompleteTime(refundPO.getCompleteTime());

        // 处理凭证图片
        if (StringUtils.hasText(refundPO.getEvidenceImages())) {
            refundVO.setEvidenceImages(refundPO.getEvidenceImages().split(","));
        }
        
        // 处理退款项
        if (refundItemPOs != null && !refundItemPOs.isEmpty()) {
            List<OrderRefundItemVO> refundItemVOs = new ArrayList<>();
            for (OrderRefundItemPO itemPO : refundItemPOs) {
                OrderRefundItemVO itemVO = new OrderRefundItemVO();
                itemVO.setId(itemPO.getId());
                itemVO.setRefundId(itemPO.getRefundId());
                itemVO.setOrderItemId(itemPO.getOrderItemId());
                itemVO.setItemName(itemPO.getItemName());
                itemVO.setSkuName(itemPO.getSkuName());
                itemVO.setSmc(itemPO.getSmc());
                itemVO.setSku(itemPO.getSku());
                itemVO.setRefundQty(itemPO.getRefundQty());
                itemVO.setPrice(itemPO.getPrice());
                itemVO.setRefundAmount(itemPO.getRefundAmount());
                itemVO.setRemark(itemPO.getRemark());
                refundItemVOs.add(itemVO);
            }
            refundVO.setRefundItems(refundItemVOs);
        }

        return refundVO;
    }

    /**
     * 转换为OrderDetailVO
     */
    private OrderListVO convertToOrderListVO(OrderPO orderPO) {
        OrderListVO listVO = new OrderListVO();
        listVO.setId(orderPO.getId());
        listVO.setOrderNo(orderPO.getOrderNo());
        listVO.setUserId(orderPO.getUserId());
        listVO.setStatus(orderPO.getStatus());
        listVO.setStatusMsg(OrderStatusType.getStatusByCode(orderPO.getStatus()).getMsg());
        listVO.setPayAmount(orderPO.getPayAmount());
        listVO.setAmount(orderPO.getAmount());
        listVO.setLogisticsAmount(BigDecimal.ZERO); // OrderPO中没有此字段，使用默认值
        listVO.setRemark(orderPO.getRemark());
        listVO.setInnerRemark(orderPO.getInnerRemark());
        listVO.setPayMethod(orderPO.getPayMethod());
        listVO.setPayNo(orderPO.getPayNo());
        listVO.setLogisticsCompanyCode(orderPO.getLogisticsCompanyCode());
        listVO.setLogisticsCompany(orderPO.getLogisticsCompany());
        listVO.setLogisticsNo(orderPO.getLogisticsNo());
        listVO.setLogisticsEstimatedPrice(orderPO.getLogisticsEstimatedPrice());
        listVO.setCreateTime(ZonedDateTime.now()); // 临时设置，实际应该从orderPO获取
        listVO.setDeliveryTime(orderPO.getExpressTime());
        listVO.setConfirmTime(orderPO.getConfirmTime());

        // 设置地址信息
        if (orderPO.getAddress() != null) {
            OrderListVO.OrderAddressVO addressVO = new OrderListVO.OrderAddressVO();
            addressVO.setUserName(orderPO.getAddress().getUserName());
            addressVO.setTelNumber(orderPO.getAddress().getTelNumber());
            addressVO.setPostalCode(orderPO.getAddress().getPostalCode());
            addressVO.setProvinceName(orderPO.getAddress().getProvinceName());
            addressVO.setCityName(orderPO.getAddress().getCityName());
            addressVO.setCountyName(orderPO.getAddress().getCountyName());
            listVO.setAddress(addressVO);
        }

        // 查询订单商品
        List<OrderItemPO> orderItemPOs = orderItemMapper.selectList(
            new LambdaQueryWrapper<OrderItemPO>()
                .eq(OrderItemPO::getOrderId, orderPO.getId())
        );

        // 设置订单商品
        List<OrderListVO.OrderItemDetailVO> orderItemVOs = orderItemPOs.stream().map(itemPO -> {
            OrderListVO.OrderItemDetailVO itemVO = new OrderListVO.OrderItemDetailVO();
            itemVO.setId(itemPO.getId());
            itemVO.setItemName(itemPO.getItemName());
            itemVO.setSkuName(itemPO.getSkuName());
            itemVO.setSmc(itemPO.getSmc());
            itemVO.setSku(itemPO.getSku());
            itemVO.setCategoryId(itemPO.getCategoryId());
            itemVO.setCategoryName(itemPO.getCategoryName());
            itemVO.setUnit(itemPO.getUnit());
            itemVO.setProductPic(itemPO.getProductPic());
            itemVO.setProductPicSmall(itemPO.getProductPicSmall());
            itemVO.setQty(itemPO.getQty());
            itemVO.setPrice(itemPO.getPrice());
            itemVO.setOriginalPrice(itemPO.getOriginalPrice());
            itemVO.setRemark(itemPO.getRemark());
            itemVO.setInnerRemark(itemPO.getInnerRemark());
            return itemVO;
        }).collect(Collectors.toList());

        listVO.setOrderItems(orderItemVOs);

        return listVO;
    }

    private OrderDetailVO convertToOrderDetailVO(OrderPO orderPO) {
        // 先转换为OrderListVO
        OrderListVO listVO = convertToOrderListVO(orderPO);
        
        // 创建OrderDetailVO并复制OrderListVO的属性
        OrderDetailVO detailVO = new OrderDetailVO();
        detailVO.setId(listVO.getId());
        detailVO.setOrderNo(listVO.getOrderNo());
        detailVO.setUserId(listVO.getUserId());
        detailVO.setStatus(listVO.getStatus());
        detailVO.setStatusMsg(listVO.getStatusMsg());
        detailVO.setPayAmount(listVO.getPayAmount());
        detailVO.setAmount(listVO.getAmount());
        detailVO.setLogisticsAmount(listVO.getLogisticsAmount());
        detailVO.setRemark(listVO.getRemark());
        detailVO.setInnerRemark(listVO.getInnerRemark());
        detailVO.setPayMethod(listVO.getPayMethod());
        detailVO.setPayNo(listVO.getPayNo());
        detailVO.setLogisticsCompanyCode(listVO.getLogisticsCompanyCode());
        detailVO.setLogisticsCompany(listVO.getLogisticsCompany());
        detailVO.setLogisticsNo(listVO.getLogisticsNo());
        detailVO.setLogisticsEstimatedPrice(listVO.getLogisticsEstimatedPrice());
        detailVO.setCreateTime(listVO.getCreateTime());
        detailVO.setDeliveryTime(listVO.getDeliveryTime());
        detailVO.setConfirmTime(listVO.getConfirmTime());
        detailVO.setAddress(listVO.getAddress());
        detailVO.setOrderItems(listVO.getOrderItems());

        // 尝试获取物流信息（仅当订单已发货且有物流单号时）
        if (orderPO.getExpressTime() != null && StringUtils.hasText(orderPO.getLogisticsNo())) {
            try {
                R<ExpressOrderVO> expressResult = expressFeignClient.get(
                    null, null, orderPO.getLogisticsNo(), null);
                if (expressResult != null && "200".equals(expressResult.getCode()) && expressResult.getData() != null) {
                    detailVO.setExpressOrder(expressResult.getData());
                }
            } catch (Exception e) {
                log.warn("获取物流信息失败，订单号: {}, 物流单号: {}, 错误: {}", 
                    orderPO.getOrderNo(), orderPO.getLogisticsNo(), e.getMessage());
                // 物流信息获取失败时不设置，保持为null
            }
        }

        // TODO: 查询退款信息
        detailVO.setRefunds(new ArrayList<>());

        return detailVO;
    }

    /**
     * 验证订单归属权限
     * @param orderPO 订单信息
     * @param sessionInfo 用户会话信息
     * @param errorMessage 错误信息
     */
    private void validateOrderOwnership(OrderPO orderPO, FoSessionInfoDTO sessionInfo, String errorMessage) {
        if (orderPO == null) {
            throw new ServiceException(BasicErrorCode.NO_RESOURCE, "订单不存在");
        }
        if (!orderPO.getUserId().equals(sessionInfo.getUserId())) {
            throw new ServiceException(BasicErrorCode.NO_PERMISSION, errorMessage);
        }
    }

    /**
     * 验证退款归属权限
     * @param refundPO 退款信息
     * @param sessionInfo 用户会话信息
     * @param errorMessage 错误信息
     */
    private void validateRefundOwnership(OrderRefundPO refundPO, FoSessionInfoDTO sessionInfo, String errorMessage) {
        if (refundPO == null) {
            throw new ServiceException(BasicErrorCode.NO_RESOURCE, "退款记录不存在");
        }
        if (!refundPO.getUserId().equals(sessionInfo.getUserId())) {
            throw new ServiceException(BasicErrorCode.NO_PERMISSION, errorMessage);
        }
    }

    // ========== 支付回调接口实现 ==========

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handlePaymentCallback(TransactionUpdateEventDTO transactionUpdateEventDTO) {
        log.info("开始处理支付回调: orderNo={}, transactionNo={}, status={}", 
                transactionUpdateEventDTO.getOrderNo(), 
                transactionUpdateEventDTO.getTransactionNo(), 
                transactionUpdateEventDTO.getStatus());

        // 根据订单号查询订单
        OrderPO orderPO = orderMapper.selectOne(
                new LambdaQueryWrapper<OrderPO>()
                        .eq(OrderPO::getOrderNo, transactionUpdateEventDTO.getOrderNo()));

        if (orderPO == null) {
            log.error("支付回调失败: 订单不存在, orderNo={}", transactionUpdateEventDTO.getOrderNo());
            throw new ServiceException(BasicErrorCode.NO_RESOURCE, "订单不存在");
        }

        // 检查订单状态是否允许支付
        if (!OrderStatusType.UNPAY.getCode().equals(orderPO.getStatus())) {
            log.warn("支付回调失败: 订单状态不允许支付, orderNo={}, currentStatus={}",
                    transactionUpdateEventDTO.getOrderNo(), orderPO.getStatus());
            throw new ServiceException(DistributionErrorCode.ORDER_STATUS_INVALID, "订单状态不允许支付");
        }

        // 根据支付状态处理订单
        TransactionStatus status = transactionUpdateEventDTO.getStatus();
        if (Objects.requireNonNull(status) == TransactionStatus.SUCCESS) {
            handlePaymentSuccess(orderPO, transactionUpdateEventDTO);
        }
    }

    /**
     * 处理支付成功
     */
    private void handlePaymentSuccess(OrderPO orderPO, TransactionUpdateEventDTO transactionUpdateEventDTO) {
        log.info("处理支付成功: orderNo={}, transactionNo={}, amount={}", 
                orderPO.getOrderNo(), 
                transactionUpdateEventDTO.getTransactionNo(), 
                transactionUpdateEventDTO.getAmount());

        // 更新订单状态为待出库
        orderPO.setStatus(OrderStatusType.WAIT_STOCK.getCode());
        orderPO.setPayMethod(transactionUpdateEventDTO.getProvider().getType());
        orderPO.setPayNo(transactionUpdateEventDTO.getTransactionNo());
        if (transactionUpdateEventDTO.getPaymentTime() != null) {
            orderPO.setPayTime(transactionUpdateEventDTO.getPaymentTime().toLocalDateTime());
        }

        // 更新订单信息
        orderMapper.updateById(orderPO);

        log.info("支付成功处理完成: orderNo={}, 订单状态已更新为待出库", orderPO.getOrderNo());
    }

    /**
     * 订单商品信息内部类
     */
    @Setter
    @Getter
    private static class OrderItemInfo {
        // Getters and Setters
        private String smc;
        private String sku;
        private Integer qty;
        private Boolean stockable;
        private String itemName;
        private String skuName;
        private BigDecimal price;
        private BigDecimal originalPrice;
        private String categoryId;
        private String categoryName;
        private String unit;
        private String productPic;
        private String productPicSmall;

    }
}