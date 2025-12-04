package com.dobbinsoft.gus.distribution.service;

import com.dobbinsoft.gus.common.model.vo.PageResult;
import com.dobbinsoft.gus.distribution.data.dto.order.FoOrderSearchDTO;
import com.dobbinsoft.gus.distribution.data.dto.order.OrderPrepayDTO;
import com.dobbinsoft.gus.distribution.data.dto.order.OrderRefundApprovalDTO;
import com.dobbinsoft.gus.distribution.data.dto.order.OrderRefundApplyDTO;
import com.dobbinsoft.gus.distribution.data.dto.order.OrderSearchDTO;
import com.dobbinsoft.gus.distribution.data.dto.order.OrderSubmitDTO;
import com.dobbinsoft.gus.distribution.data.vo.order.OrderDetailVO;
import com.dobbinsoft.gus.distribution.data.vo.order.OrderListVO;
import com.dobbinsoft.gus.distribution.data.vo.order.OrderPreviewVO;
import com.dobbinsoft.gus.distribution.data.vo.order.OrderPrepayVO;
import com.dobbinsoft.gus.distribution.data.vo.order.OrderRefundVO;
import com.dobbinsoft.gus.distribution.data.vo.order.OrderVO;
import com.dobbinsoft.gus.distribution.client.gus.payment.model.TransactionUpdateEventDTO;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "订单服务", description = "订单相关业务逻辑")
public interface OrderService {

    /**
     * 订单预览
     * @param submitDTO 订单提交信息
     * @return 订单预览信息
     */
    OrderPreviewVO preview(OrderSubmitDTO submitDTO);

    /**
     * 提交订单
     *
     * @param submitDTO    订单提交信息
     * @param locationCode
     * @return 订单信息
     */
    OrderVO submit(OrderSubmitDTO submitDTO, String locationCode);

    /**
     * 订单预支付
     * @param prepayDTO 预支付请求信息
     * @return 预支付信息
     */
    OrderPrepayVO prepay(OrderPrepayDTO prepayDTO);

    /**
     * 根据订单号获取订单详情
     * @param orderNo 订单号
     * @return 订单详情
     */
    OrderDetailVO getByOrderNo(String orderNo);

    // ========== 后台管理接口 ==========

    /**
     * 分页查询订单列表
     * @param searchDTO 搜索条件
     * @return 订单分页结果
     */
    PageResult<OrderListVO> page(OrderSearchDTO searchDTO);

    /**
     * 根据订单号获取订单详情（后台管理）
     * @param orderNo 订单号
     * @return 订单详情
     */
    OrderDetailVO getDetailByOrderNo(String orderNo);

    /**
     * 确认收货
     * @param orderNo 订单号
     */
    void confirmReceipt(String orderNo);

    /**
     * 审核退款申请
     * @param orderNo 订单号
     * @param approvalDTO 审核信息
     */
    void approveRefund(String orderNo, OrderRefundApprovalDTO approvalDTO);

    // ========== 前台退款接口 ==========

    /**
     * 申请退款
     * @param applyDTO 退款申请信息
     * @return 退款信息
     */
    OrderRefundVO applyRefund(OrderRefundApplyDTO applyDTO);

    /**
     * 获取用户的退款列表
     * @param orderNo 订单号（可选）
     * @return 退款列表
     */
    List<OrderRefundVO> getUserRefunds(String orderNo);

    /**
     * 取消退款申请
     * @param refundId 退款单ID
     */
    void cancelRefund(String refundId);

    // ========== 前台订单查询接口 ==========

    /**
     * 获取当前登录用户的订单列表
     * @param searchDTO 搜索条件
     * @return 订单分页结果
     */
    PageResult<OrderListVO> getUserOrders(FoOrderSearchDTO searchDTO);

    // ========== 支付回调接口 ==========

    /**
     * 处理支付回调
     * @param transactionUpdateEventDTO 支付回调事件数据
     */
    void handlePaymentCallback(TransactionUpdateEventDTO transactionUpdateEventDTO);

} 