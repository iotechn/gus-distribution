package com.dobbinsoft.gus.distribution.service.impl;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dobbinsoft.gus.common.model.vo.PageResult;
import com.dobbinsoft.gus.distribution.client.gus.location.LocationFeignClient;
import com.dobbinsoft.gus.distribution.client.gus.location.model.LocationVO;
import com.dobbinsoft.gus.distribution.client.gus.logistics.DeliveryFeeFeignClient;
import com.dobbinsoft.gus.distribution.client.gus.logistics.DeliveryOrderFeignClient;
import com.dobbinsoft.gus.distribution.client.gus.logistics.model.DeliveryFeePreviewDTO;
import com.dobbinsoft.gus.distribution.client.gus.logistics.model.DeliveryFeePreviewVO;
import com.dobbinsoft.gus.distribution.client.gus.logistics.model.DeliveryOrderVO;
import com.dobbinsoft.gus.distribution.client.gus.payment.TransactionFeignClient;
import com.dobbinsoft.gus.distribution.client.gus.payment.model.TransactionCreateDTO;
import com.dobbinsoft.gus.distribution.client.gus.payment.model.TransactionStatus;
import com.dobbinsoft.gus.distribution.client.gus.payment.model.TransactionUpdateEventDTO;
import com.dobbinsoft.gus.distribution.client.gus.product.ProductItemFeignClient;
import com.dobbinsoft.gus.distribution.client.gus.product.ProductStockFeignClient;
import com.dobbinsoft.gus.distribution.client.gus.product.model.BatchStockAdjustDTO;
import com.dobbinsoft.gus.distribution.client.gus.product.model.ItemSearchDTO;
import com.dobbinsoft.gus.distribution.client.gus.product.model.ItemVO;
import com.dobbinsoft.gus.distribution.client.gus.product.model.ListStockVO;
import com.dobbinsoft.gus.distribution.client.gus.product.model.StockSearchDTO;
import com.dobbinsoft.gus.distribution.data.dto.order.FoOrderSearchDTO;
import com.dobbinsoft.gus.distribution.data.dto.order.OrderPrepayDTO;
import com.dobbinsoft.gus.distribution.data.dto.order.OrderRefundApplyDTO;
import com.dobbinsoft.gus.distribution.data.dto.order.OrderRefundApprovalDTO;
import com.dobbinsoft.gus.distribution.data.dto.order.OrderRefundItemDTO;
import com.dobbinsoft.gus.distribution.data.dto.order.OrderSearchDTO;
import com.dobbinsoft.gus.distribution.data.dto.order.OrderSubmitDTO;
import com.dobbinsoft.gus.distribution.data.dto.session.FoSessionInfoDTO;
import com.dobbinsoft.gus.distribution.data.enums.CurrencyCode;
import com.dobbinsoft.gus.distribution.data.enums.OrderStatusType;
import com.dobbinsoft.gus.distribution.data.enums.RefundStatusType;
import com.dobbinsoft.gus.distribution.data.enums.UserSrcType;
import com.dobbinsoft.gus.distribution.data.exception.DistributionErrorCode;
import com.dobbinsoft.gus.distribution.data.po.AddressPO;
import com.dobbinsoft.gus.distribution.data.po.CartItemPO;
import com.dobbinsoft.gus.distribution.data.po.OrderItemPO;
import com.dobbinsoft.gus.distribution.data.po.OrderPO;
import com.dobbinsoft.gus.distribution.data.po.OrderRefundItemPO;
import com.dobbinsoft.gus.distribution.data.po.OrderRefundPO;
import com.dobbinsoft.gus.distribution.data.po.UserSocialPO;
import com.dobbinsoft.gus.distribution.data.vo.order.OrderDetailVO;
import com.dobbinsoft.gus.distribution.data.vo.order.OrderListVO;
import com.dobbinsoft.gus.distribution.data.vo.order.OrderPrepayVO;
import com.dobbinsoft.gus.distribution.data.vo.order.OrderPreviewVO;
import com.dobbinsoft.gus.distribution.data.vo.order.OrderRefundItemVO;
import com.dobbinsoft.gus.distribution.data.vo.order.OrderRefundVO;
import com.dobbinsoft.gus.distribution.data.vo.order.OrderVO;
import com.dobbinsoft.gus.distribution.mapper.AddressMapper;
import com.dobbinsoft.gus.distribution.mapper.CartItemMapper;
import com.dobbinsoft.gus.distribution.mapper.OrderItemMapper;
import com.dobbinsoft.gus.distribution.mapper.OrderMapper;
import com.dobbinsoft.gus.distribution.mapper.OrderRefundItemMapper;
import com.dobbinsoft.gus.distribution.mapper.OrderRefundMapper;
import com.dobbinsoft.gus.distribution.mapper.UserSocialMapper;
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
    private final TransactionFeignClient transactionFeignClient;
    private final DeliveryFeeFeignClient deliveryFeeFeignClient;
    private final DeliveryOrderFeignClient deliveryOrderFeignClient;
    private final LocationFeignClient locationFeignClient;

    @Override
    public OrderPreviewVO preview(OrderSubmitDTO submitDTO, String locationCode) {
        // 获取用户信息
        FoSessionInfoDTO sessionInfo = SessionUtils.getFoSession();

        // 获取订单商品信息（与提交逻辑一致）
        List<OrderItemInfo> orderItemInfos = getOrderItemInfos(submitDTO, locationCode);

        // 计算商品总金额
        BigDecimal goodsTotalAmount = orderItemInfos.stream()
                .map(item -> item.getPrice().multiply(item.getQty()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 校验并获取收货地址（与提交逻辑保持一致）
        AddressPO addressPO = addressMapper.selectById(submitDTO.getAddressId());
        if (addressPO == null || !addressPO.getUserId().equals(sessionInfo.getUserId())) {
            throw new ServiceException(BasicErrorCode.NO_RESOURCE);
        }

        // 通过门店编码查询门店坐标，并调用配送费服务进行预估
        R<LocationVO> locationResult = locationFeignClient.detail(locationCode);
        if (!BasicErrorCode.SUCCESS.getCode().equals(locationResult.getCode())) {
            throw new ServiceException(locationResult.getCode(), locationResult.getMessage());
        }
        LocationVO locationVO = locationResult.getData();
        DeliveryFeePreviewDTO deliveryFeePreviewDTO = new DeliveryFeePreviewDTO();
        deliveryFeePreviewDTO.setLocationCode(locationCode);
        deliveryFeePreviewDTO.setOrderAmount(goodsTotalAmount);
        deliveryFeePreviewDTO.setStartLatitude(locationVO.getLatitude());
        deliveryFeePreviewDTO.setStartLongitude(locationVO.getLongitude());
        deliveryFeePreviewDTO.setEndLatitude(addressPO.getLatitude());
        deliveryFeePreviewDTO.setEndLongitude(addressPO.getLongitude());

        R<DeliveryFeePreviewVO> deliveryFeePreviewResult = deliveryFeeFeignClient.previewDeliveryFee(deliveryFeePreviewDTO);
        if (!BasicErrorCode.SUCCESS.getCode().equals(deliveryFeePreviewResult.getCode())) {
            throw new ServiceException(deliveryFeePreviewResult.getCode(), deliveryFeePreviewResult.getMessage());
        }
        DeliveryFeePreviewVO deliveryFeePreviewVO = deliveryFeePreviewResult.getData();
        BigDecimal deliveryAmount = deliveryFeePreviewVO.getActualFee();

        // 计算应付总额 = 商品总额 + 配送费
        BigDecimal payAmount = goodsTotalAmount.add(deliveryAmount);
        if (payAmount.compareTo(BigDecimal.ZERO) < 0) {
            payAmount = BigDecimal.ZERO;
        }

        OrderPreviewVO previewVO = new OrderPreviewVO();
        previewVO.setDeliveryAmount(deliveryAmount);
        previewVO.setTotalAmount(payAmount);
        previewVO.setDeliveryDistance(deliveryFeePreviewVO.getDistance());
        return previewVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVO submit(OrderSubmitDTO submitDTO, String locationCode) {
        // 获取用户信息
        FoSessionInfoDTO sessionInfo = SessionUtils.getFoSession();

        // 获取订单商品信息
        List<OrderItemInfo> orderItemInfos = getOrderItemInfos(submitDTO, locationCode);
        
        // 验证库存
        validateStock(orderItemInfos);

        // 计算商品总金额
        BigDecimal totalAmount = orderItemInfos.stream()
                .map(item -> item.getPrice().multiply(item.getQty()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 获取收货地址
        AddressPO addressPO = addressMapper.selectById(submitDTO.getAddressId());
        if (addressPO == null || !addressPO.getUserId().equals(sessionInfo.getUserId())) {
            throw new ServiceException(BasicErrorCode.NO_RESOURCE);
        }

        // 物流费用（暂时为0）
        R<LocationVO> locationResult = locationFeignClient.detail(locationCode);
        if (!BasicErrorCode.SUCCESS.getCode().equals(locationResult.getCode())) {
            throw new ServiceException(locationResult.getCode(), locationResult.getMessage());
        }
        LocationVO locationVO = locationResult.getData();
        DeliveryFeePreviewDTO deliveryFeePreviewDTO = new DeliveryFeePreviewDTO();
        deliveryFeePreviewDTO.setLocationCode(locationCode);
        deliveryFeePreviewDTO.setOrderAmount(totalAmount);
        deliveryFeePreviewDTO.setStartLatitude(locationVO.getLatitude());
        deliveryFeePreviewDTO.setStartLongitude(locationVO.getLongitude());
        deliveryFeePreviewDTO.setEndLatitude(addressPO.getLatitude());
        deliveryFeePreviewDTO.setEndLongitude(addressPO.getLongitude());

        R<DeliveryFeePreviewVO> deliveryFeePreviewResult = deliveryFeeFeignClient.previewDeliveryFee(deliveryFeePreviewDTO);
        if (!BasicErrorCode.SUCCESS.getCode().equals(deliveryFeePreviewResult.getCode())) {
            throw new ServiceException(deliveryFeePreviewResult.getCode(), deliveryFeePreviewResult.getMessage());
        }
        DeliveryFeePreviewVO deliveryFeePreviewVO = deliveryFeePreviewResult.getData();

        // 计算支付金额
        BigDecimal payAmount = totalAmount.add(deliveryFeePreviewVO.getActualFee());
        if (payAmount.compareTo(BigDecimal.ZERO) < 0) {
            payAmount = BigDecimal.ZERO;
        }



        // 创建订单
        OrderPO orderPO = new OrderPO();
        orderPO.setId(UuidWorker.nextId());
        orderPO.setOrderNo(generateOrderNo());
        orderPO.setUserId(sessionInfo.getUserId());
        orderPO.setStatus(OrderStatusType.UNPAY.getCode());
        orderPO.setAmount(totalAmount);
        orderPO.setPayAmount(payAmount);
        orderPO.setRemark(submitDTO.getRemark());
        // 记录下单门店编码
        orderPO.setLocationCode(locationCode);

        // 构建搜索关键字（冗余商品信息用于搜索）
        String searchKeyword = buildSearchKeyword(orderItemInfos);
        orderPO.setSearchKeyword(searchKeyword);

        // 设置收货地址
        OrderPO.Address orderAddress = new OrderPO.Address();
        orderAddress.setUserName(addressPO.getUserName());
        orderAddress.setTelNumber(addressPO.getTelNumber());
        orderAddress.setPostalCode(addressPO.getPostalCode());
        orderAddress.setProvinceName(addressPO.getProvinceName());
        orderAddress.setCityName(addressPO.getCityName());
        orderAddress.setCountyName(addressPO.getCountyName());
        orderAddress.setDetailAddress(addressPO.getDetailAddress());
        orderAddress.setLongitude(addressPO.getLongitude());
        orderAddress.setLatitude(addressPO.getLatitude());
        orderPO.setAddress(orderAddress);

        // 保存订单
        orderMapper.insert(orderPO);

        // 创建订单商品
        List<OrderItemPO> orderItemPOs = new ArrayList<>();
        for (OrderItemInfo itemInfo : orderItemInfos) {
            OrderItemPO orderItemPO = new OrderItemPO();
            orderItemPO.setId(UuidWorker.nextId());
            orderItemPO.setOrderId(orderPO.getId());
            orderItemPO.setItemName(itemInfo.getItemName());
            orderItemPO.setSkuName(itemInfo.getSkuName());
            orderItemPO.setSmc(itemInfo.getSmc());
            orderItemPO.setSku(itemInfo.getSku());
            orderItemPO.setUnit(itemInfo.getUnit());
            orderItemPO.setProductPic(itemInfo.getProductPic());
            orderItemPO.setProductPicSmall(itemInfo.getProductPicSmall());
            orderItemPO.setQty(itemInfo.getQty());
            orderItemPO.setPrice(itemInfo.getPrice());
            orderItemPOs.add(orderItemPO);
        }

        // 批量保存订单商品
        for (OrderItemPO orderItemPO : orderItemPOs) {
            orderItemMapper.insert(orderItemPO);
        }

        // 如果是从购物车提交，删除购物车商品
        if (!CollectionUtils.isEmpty(submitDTO.getCartItemIds())) {
            QueryWrapper<CartItemPO> deleteWrapper = new QueryWrapper<>();
            deleteWrapper.in("id", submitDTO.getCartItemIds())
                    .eq("cart_id", sessionInfo.getUserId());
            cartItemMapper.delete(deleteWrapper);
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
                new QueryWrapper<OrderPO>()
                        .eq("order_no", orderNo));

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
                new QueryWrapper<OrderPO>()
                        .eq("order_no", prepayDTO.getOrderNo()));

        // 验证订单归属权限
        validateOrderOwnership(orderPO, sessionInfo, "无权限操作此订单");

        // 检查订单状态是否可以支付
        if (!OrderStatusType.UNPAY.getCode().equals(orderPO.getStatus())) {
            throw new ServiceException(DistributionErrorCode.ORDER_STATUS_INVALID, "订单状态不允许支付");
        }

        // 查询订单商品信息
        List<OrderItemPO> orderItemPOs = orderItemMapper.selectList(
                new QueryWrapper<OrderItemPO>()
                        .eq("order_id", orderPO.getId()));

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
            UserSocialPO userSocialPO = userSocialMapper.selectOne(new QueryWrapper<UserSocialPO>()
                    .eq("src", UserSrcType.DISTRIBUTION_WECHAT_WEB.name())
                    .eq("user_id", orderPO.getUserId()));
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
            item.setQuantity(itemPO.getQty());
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
    private List<OrderItemInfo> getOrderItemInfos(OrderSubmitDTO submitDTO, String locationCode) {
        List<OrderItemInfo> orderItemInfos = new ArrayList<>();

        // 处理直接提交的商品
        if (!CollectionUtils.isEmpty(submitDTO.getOrderItems())) {
            // 获取SKU列表
            List<String> skuIds = submitDTO.getOrderItems().stream()
                    .map(OrderSubmitDTO.OrderItem::getSku)
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

            // 查询库存与价格信息（按 locationCode + sku 检索）
            StockSearchDTO stockSearchDTO = new StockSearchDTO();
            stockSearchDTO.setLocationCode(Collections.singletonList(locationCode));
            stockSearchDTO.setSku(skuIds);
            stockSearchDTO.setPageNum(1);
            stockSearchDTO.setPageSize(100);
            R<PageResult<ListStockVO>> stockResp = productStockFeignClient.search(stockSearchDTO);
            if (!BasicErrorCode.SUCCESS.getCode().equals(stockResp.getCode()) || stockResp.getData() == null) {
                throw new ServiceException(stockResp.getCode(), stockResp.getMessage());
            }
            Map<String, ListStockVO> stockBySku = Optional.ofNullable(stockResp.getData().getData())
                    .orElse(Collections.emptyList())
                    .stream()
                    .collect(Collectors.toMap(ListStockVO::getSku, s -> s));

            // 构建订单商品信息
            for (OrderSubmitDTO.OrderItem orderItem : submitDTO.getOrderItems()) {
                ItemVO itemVO = itemMap.values().stream()
                        .filter(item -> item.getSkus().stream()
                                .anyMatch(sku -> sku.getSku().equals(orderItem.getSku())))
                        .findFirst()
                        .orElse(null);

                if (itemVO == null) {
                    throw new ServiceException(DistributionErrorCode.ITEM_NOT_FOUND);
                }

                // 获取SKU信息
                ItemVO.ItemSkuVO skuVO = itemVO.getSkus().stream()
                        .filter(sku -> sku.getSku().equals(orderItem.getSku()))
                        .findFirst()
                        .orElse(null);

                if (skuVO == null) {
                    throw new ServiceException(DistributionErrorCode.INVALID_SKU);
                }

                // 获取库存价格信息
                ListStockVO stockVO = stockBySku.get(orderItem.getSku());
                if (stockVO == null) {
                    throw new ServiceException(BasicErrorCode.NO_RESOURCE, "库存或价格信息不存在: " + orderItem.getSku());
                }

                OrderItemInfo itemInfo = new OrderItemInfo();
                itemInfo.setLocationCode(locationCode);
                itemInfo.setSmc(itemVO.getSmc());
                itemInfo.setSku(orderItem.getSku());
                itemInfo.setQty(orderItem.getQty());
                itemInfo.setStockable(itemVO.getStockable());
                
                // 设置商品信息（这里简化处理，实际应该从商品详情中获取价格等信息）
                itemInfo.setItemName(itemVO.getSmc());
                itemInfo.setSkuName(orderItem.getSku());
                itemInfo.setPrice(stockVO.getPrice());
                itemInfo.setAvailableQuantity(stockVO.getQuantity());

                if (itemVO.getUnitGroup() != null) {
                    itemInfo.setUnit(itemVO.getUnitGroup().getName());
                }

                if (!CollectionUtils.isEmpty(itemVO.getImages())) {
                    itemInfo.setProductPic(itemVO.getImages().getFirst());
                    itemInfo.setProductPicSmall(itemVO.getImages().getFirst());
                }

                orderItemInfos.add(itemInfo);
            }
        }

        // 验证商品数量
        if (CollectionUtils.isEmpty(orderItemInfos)) {
            throw new ServiceException(DistributionErrorCode.ORDER_ITEMS_EMPTY);
        }

        // 验证SKU数量不超过100
        int totalSkuCount = orderItemInfos.size();
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
            if (Boolean.TRUE.equals(itemInfo.getStockable())) {
                // 对于库存商品，需要验证库存是否充足
                if (itemInfo.getAvailableQuantity() == null) {
                    throw new ServiceException(BasicErrorCode.SYSTEM_ERROR, "缺少库存信息: " + itemInfo.getSku());
                }
                if (itemInfo.getAvailableQuantity().compareTo(itemInfo.getQty()) < 0) {
                    throw new ServiceException(BasicErrorCode.PARAMERROR, "库存不足: " + itemInfo.getSku());
                }
                log.info("验证库存: locationCode={}, sku={}, 需要数量={}, 可用={}", itemInfo.getLocationCode(), itemInfo.getSku(), itemInfo.getQty(), itemInfo.getAvailableQuantity());
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
                itemDTO.setLocationCode(itemInfo.getLocationCode());
                itemDTO.setSku(itemInfo.getSku());
                itemDTO.setOperation(BatchStockAdjustDTO.Operation.SUBTRACT);
                itemDTO.setQuantity(itemInfo.getQty());
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
        orderVO.setDeliveryTime(orderPO.getDeliveryTime());
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
            itemVO.setUnit(itemPO.getUnit());
            itemVO.setProductPic(itemPO.getProductPic());
            itemVO.setProductPicSmall(itemPO.getProductPicSmall());
            itemVO.setQty(itemPO.getQty());
            itemVO.setPrice(itemPO.getPrice());
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
        
        QueryWrapper<OrderPO> queryWrapper = new QueryWrapper<>();
        
        // 订单号查询
        if (StringUtils.hasText(searchDTO.getOrderNo())) {
            queryWrapper.like("order_no", searchDTO.getOrderNo());
        }

        // 订单状态查询
        if (searchDTO.getStatus() != null) {
            queryWrapper.eq("status", searchDTO.getStatus());
        }

        // 物流公司查询
        if (StringUtils.hasText(searchDTO.getLogisticsCompany())) {
            queryWrapper.like("logistics_company", searchDTO.getLogisticsCompany());
        }
        
        // 物流单号查询
        if (StringUtils.hasText(searchDTO.getLogisticsNo())) {
            queryWrapper.like("logistics_no", searchDTO.getLogisticsNo());
        }
        
        // 创建时间范围查询
        if (searchDTO.getPayTimeStart() != null) {
            queryWrapper.ge("pay_time", searchDTO.getPayTimeStart());
        }
        if (searchDTO.getPayTimeEnd() != null) {
            queryWrapper.le("pay_time", searchDTO.getPayTimeEnd());
        }
        
        // 发货时间范围查询
        if (searchDTO.getDeliveryTimeStart() != null) {
            queryWrapper.ge("express_time", searchDTO.getDeliveryTimeStart());
        }
        if (searchDTO.getDeliveryTimeEnd() != null) {
            queryWrapper.le("express_time", searchDTO.getDeliveryTimeEnd());
        }
        
        // 按创建时间倒序排列
        queryWrapper.orderByDesc("id");
        
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
            new QueryWrapper<OrderPO>()
                .eq("order_no", orderNo)
        );
        if (orderPO == null) {
            throw new ServiceException(BasicErrorCode.NO_RESOURCE);
        }
        
        return convertToOrderDetailVO(orderPO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmReceipt(String orderNo) {
        // 获取用户信息
        FoSessionInfoDTO sessionInfo = SessionUtils.getFoSession();
        
        OrderPO orderPO = orderMapper.selectOne(
            new QueryWrapper<OrderPO>()
                .eq("order_no", orderNo)
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
                new QueryWrapper<OrderPO>()
                        .eq("order_no", applyDTO.getOrderNo()));

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
                    new QueryWrapper<OrderItemPO>()
                            .eq("id", itemDTO.getOrderItemId())
                            .eq("order_id", orderPO.getId()));
            if (orderItemPO == null) {
                throw new ServiceException(BasicErrorCode.NO_RESOURCE, "订单商品项不存在: " + itemDTO.getOrderItemId());
            }

            // 查询已申请退款的数量（包括待处理、已批准和处理中的退款）
            List<OrderRefundItemPO> existingRefundItems = orderRefundItemMapper.selectList(
                    new QueryWrapper<OrderRefundItemPO>()
                            .eq("order_item_id", itemDTO.getOrderItemId()));
            
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
            if (java.math.BigDecimal.valueOf(totalRefundQty).compareTo(orderItemPO.getQty()) > 0) {
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

        QueryWrapper<OrderRefundPO> queryWrapper = new QueryWrapper<OrderRefundPO>()
                .eq("user_id", sessionInfo.getUserId())
                .orderByDesc("created_time");

        if (StringUtils.hasText(orderNo)) {
            queryWrapper.eq("order_no", orderNo);
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
                    new QueryWrapper<OrderRefundPO>()
                            .eq("order_id", refundPO.getOrderId())
                            .in("status",
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(String orderNo) {
        // 获取用户信息
        FoSessionInfoDTO sessionInfo = SessionUtils.getFoSession();

        // 查询订单信息
        OrderPO orderPO = orderMapper.selectOne(
                new QueryWrapper<OrderPO>()
                        .eq("order_no", orderNo));

        // 验证订单归属权限
        validateOrderOwnership(orderPO, sessionInfo, "无权限操作此订单");

        // 检查订单是否可取消
        if (!OrderStatusType.cancelable(orderPO.getStatus())) {
            throw new ServiceException(DistributionErrorCode.ORDER_STATUS_INVALID, "订单状态不允许取消");
        }

        // 查询订单商品
        List<OrderItemPO> orderItemPOs = orderItemMapper.selectList(
                new QueryWrapper<OrderItemPO>()
                        .eq("order_id", orderPO.getId()));

        // 回补库存（仅对库存型商品）
        if (!CollectionUtils.isEmpty(orderItemPOs)) {
            // 先批量查询商品，判断是否库存型
            List<String> skuList = orderItemPOs.stream().map(OrderItemPO::getSku).distinct().collect(Collectors.toList());
            ItemSearchDTO searchDTO = new ItemSearchDTO();
            searchDTO.setSku(skuList);
            searchDTO.setPageNum(1);
            searchDTO.setPageSize(Math.max(100, skuList.size()));

            R<PageResult<ItemVO>> itemResp = productItemFeignClient.search(searchDTO);
            if (!BasicErrorCode.SUCCESS.getCode().equals(itemResp.getCode()) || itemResp.getData() == null) {
                throw new ServiceException(BasicErrorCode.SYSTEM_ERROR, itemResp.getMessage());
            }
            List<ItemVO> itemVOs = Optional.ofNullable(itemResp.getData().getData()).orElse(Collections.emptyList());

            // 映射sku到是否库存型
            Set<String> stockableSkus = new HashSet<>();
            for (ItemVO itemVO : itemVOs) {
                boolean stockable = Boolean.TRUE.equals(itemVO.getStockable());
                if (stockable && itemVO.getSkus() != null) {
                    for (ItemVO.ItemSkuVO skuVO : itemVO.getSkus()) {
                        if (skuList.contains(skuVO.getSku())) {
                            stockableSkus.add(skuVO.getSku());
                        }
                    }
                }
            }

            List<BatchStockAdjustDTO.ItemDTO> adjustItems = new ArrayList<>();
            // 回补库存使用订单存储的门店编码，若缺失则尝试使用会话中的编码
            String restockLocationCode = orderPO.getLocationCode();
            for (OrderItemPO itemPO : orderItemPOs) {
                if (stockableSkus.contains(itemPO.getSku())) {
                    BatchStockAdjustDTO.ItemDTO itemDTO = new BatchStockAdjustDTO.ItemDTO();
                    itemDTO.setLocationCode(restockLocationCode);
                    itemDTO.setSku(itemPO.getSku());
                    itemDTO.setOperation(BatchStockAdjustDTO.Operation.ADD);
                    itemDTO.setQuantity(itemPO.getQty());
                    adjustItems.add(itemDTO);
                }
            }

            if (!adjustItems.isEmpty()) {
                BatchStockAdjustDTO batch = new BatchStockAdjustDTO();
                batch.setItems(adjustItems);
                R<Void> resp = productStockFeignClient.adjustStock(batch);
                if (!BasicErrorCode.SUCCESS.getCode().equals(resp.getCode())) {
                    throw new ServiceException(BasicErrorCode.SYSTEM_ERROR, resp.getMessage());
                }
            }
        }

        // 更新订单状态为已取消
        orderPO.setStatus(OrderStatusType.CANCELED.getCode());
        orderMapper.updateById(orderPO);

        log.info("用户取消订单成功: userId={}, orderNo={}", sessionInfo.getUserId(), orderNo);
    }

    public PageResult<OrderListVO> getUserOrders(FoOrderSearchDTO searchDTO) {
        // 获取当前登录用户ID
        FoSessionInfoDTO sessionInfo = SessionUtils.getFoSession();
        String userId = sessionInfo.getUserId();

        Page<OrderPO> page = new Page<>(searchDTO.getPageNum(), searchDTO.getPageSize());
        
        QueryWrapper<OrderPO> queryWrapper = new QueryWrapper<>();
        
        // 必须过滤当前用户的订单
        queryWrapper.eq("user_id", userId);

        // 订单状态过滤
        if (searchDTO.getStatus() != null) {
            queryWrapper.eq("status", searchDTO.getStatus());
        }

        // 下单时间范围过滤（使用 createdTime）
        if (searchDTO.getCreateTimeStart() != null) {
            queryWrapper.ge("created_time", searchDTO.getCreateTimeStart());
        }
        if (searchDTO.getCreateTimeEnd() != null) {
            queryWrapper.le("created_time", searchDTO.getCreateTimeEnd());
        }

        // 关键字搜索（订单号或搜索关键字字段）
        if (StringUtils.hasText(searchDTO.getKeyword())) {
            String keyword = searchDTO.getKeyword().trim();
            
            // 搜索订单号或搜索关键字字段（searchKeyword 包含商品名、SMC、SKU等信息）
            queryWrapper.and(wrapper -> wrapper
                    .like("order_no", keyword)
                    .or()
                    .like("search_keyword", keyword)
            );
        }
        
        // 按创建时间倒序排列
        queryWrapper.orderByDesc("id");
        
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
        listVO.setCreateTime(orderPO.getCreatedTime());
        listVO.setDeliveryNo(orderPO.getDeliveryNo());
        listVO.setDeliveryTime(orderPO.getDeliveryTime());
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
            new QueryWrapper<OrderItemPO>()
                .eq("order_id", orderPO.getId())
        );

        // 设置订单商品
        List<OrderListVO.OrderItemDetailVO> orderItemVOs = orderItemPOs.stream().map(itemPO -> {
            OrderListVO.OrderItemDetailVO itemVO = new OrderListVO.OrderItemDetailVO();
            itemVO.setId(itemPO.getId());
            itemVO.setItemName(itemPO.getItemName());
            itemVO.setSkuName(itemPO.getSkuName());
            itemVO.setSmc(itemPO.getSmc());
            itemVO.setSku(itemPO.getSku());
            itemVO.setUnit(itemPO.getUnit());
            itemVO.setProductPic(itemPO.getProductPic());
            itemVO.setProductPicSmall(itemPO.getProductPicSmall());
            itemVO.setQty(itemPO.getQty());
            itemVO.setPrice(itemPO.getPrice());
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
        detailVO.setDeliveryNo(listVO.getDeliveryNo());
        detailVO.setCreateTime(listVO.getCreateTime());
        detailVO.setDeliveryTime(listVO.getDeliveryTime());
        detailVO.setConfirmTime(listVO.getConfirmTime());
        detailVO.setAddress(listVO.getAddress());
        detailVO.setOrderItems(listVO.getOrderItems());

        // 尝试获取物流信息（仅当订单已发货且有物流单号时）
        if (detailVO.getDeliveryNo() != null) {
            R<DeliveryOrderVO> deliveryOrderResult = deliveryOrderFeignClient.getDeliveryOrderByDeliveryNo(detailVO.getDeliveryNo());
            if (!BasicErrorCode.SUCCESS.getCode().equals(deliveryOrderResult.getCode())) {
                throw new ServiceException(deliveryOrderResult.getCode(), deliveryOrderResult.getMessage());
            }
            DeliveryOrderVO data = deliveryOrderResult.getData();
            detailVO.setDeliveryOrder(data);
        }
        // TODO: 查询退款信息
        detailVO.setRefunds(new ArrayList<>());

        return detailVO;
    }

    /**
     * 构建搜索关键字
     * 将所有商品的相关信息（商品名、SMC、SKU、SKU名称、分类名称等）拼接后用于搜索
     * @param orderItemInfos 订单商品信息列表
     * @return 搜索关键字字符串
     */
    private String buildSearchKeyword(List<OrderItemInfo> orderItemInfos) {
        if (CollectionUtils.isEmpty(orderItemInfos)) {
            return "";
        }
        
        Set<String> keywords = new HashSet<>();
        for (OrderItemInfo itemInfo : orderItemInfos) {
            // 添加商品名称
            if (StringUtils.hasText(itemInfo.getItemName())) {
                keywords.add(itemInfo.getItemName());
            }
            // 添加SMC
            if (StringUtils.hasText(itemInfo.getSmc())) {
                keywords.add(itemInfo.getSmc());
            }
            // 添加SKU
            if (StringUtils.hasText(itemInfo.getSku())) {
                keywords.add(itemInfo.getSku());
            }
            // 添加SKU名称
            if (StringUtils.hasText(itemInfo.getSkuName())) {
                keywords.add(itemInfo.getSkuName());
            }
        }
        
        // 使用空格连接所有关键字
        return String.join(" ", keywords);
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
        log.info("[handlePaymentCallback] start hanlde: orderNo={}, transactionNo={}, status={}", 
                transactionUpdateEventDTO.getOrderNo(), 
                transactionUpdateEventDTO.getTransactionNo(), 
                transactionUpdateEventDTO.getStatus());

        // 根据订单号查询订单
        OrderPO orderPO = orderMapper.selectOne(
                new QueryWrapper<OrderPO>()
                        .eq("order_no", transactionUpdateEventDTO.getOrderNo()));

        if (orderPO == null) {
            log.error("[handlePaymentCallback] handle payment callback failed: order not found, orderNo={}", transactionUpdateEventDTO.getOrderNo());
            throw new ServiceException(BasicErrorCode.NO_RESOURCE, "订单不存在");
        }

        // 检查订单状态是否允许支付
        if (!OrderStatusType.UNPAY.getCode().equals(orderPO.getStatus())) {
            log.warn("[handlePaymentCallback] handle payment callback failed: order status not allowed, orderNo={}, currentStatus={}",
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
        log.info("[handlePaymentSuccess] handle payment success: orderNo={}, transactionNo={}, amount={}", 
                orderPO.getOrderNo(), 
                transactionUpdateEventDTO.getTransactionNo(), 
                transactionUpdateEventDTO.getAmount());

        // 更新订单状态为待出库
        orderPO.setStatus(OrderStatusType.WAIT_STOCK.getCode());
        orderPO.setPayMethod(transactionUpdateEventDTO.getProvider().getType());
        orderPO.setPayNo(transactionUpdateEventDTO.getTransactionNo());
        if (transactionUpdateEventDTO.getPaymentTime() != null) {
            orderPO.setPayTime(transactionUpdateEventDTO.getPaymentTime());
        }

        // 更新订单信息
        orderMapper.updateById(orderPO);

        log.info("[handlePaymentSuccess] handle payment success completed: orderNo={}, order status updated to wait stock", orderPO.getOrderNo());
    }

    /**
     * 订单商品信息内部类
     */
    @Setter
    @Getter
    private static class OrderItemInfo {
        private String smc;
        private String sku;
        private BigDecimal qty;
        private Boolean stockable;
        private String itemName;
        private String skuName;
        private BigDecimal price;
        private String locationCode;
        private BigDecimal availableQuantity; // 可用库存
        private String unit;
        private String productPic;
        private String productPicSmall;
    }
}