package com.dobbinsoft.gus.distribution.controller.bo;

import com.dobbinsoft.gus.common.model.vo.PageResult;
import com.dobbinsoft.gus.distribution.data.dto.order.OrderRefundApprovalDTO;
import com.dobbinsoft.gus.distribution.data.dto.order.OrderSearchDTO;
import com.dobbinsoft.gus.distribution.data.vo.order.OrderDetailVO;
import com.dobbinsoft.gus.distribution.data.vo.order.OrderListVO;
import com.dobbinsoft.gus.distribution.service.OrderService;
import com.dobbinsoft.gus.web.vo.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "后台-订单管理", description = "订单的查询、发货、退款等管理操作")
@RestController
@RequestMapping("/bo/orders")
@RequiredArgsConstructor
public class BoOrderController {

    private final OrderService orderService;

    @Operation(summary = "分页查询订单列表", description = "根据条件分页查询订单列表")
    @GetMapping
    public R<PageResult<OrderListVO>> page(
            @Parameter(description = "查询条件", required = true)
            @Valid OrderSearchDTO searchDTO) {
        PageResult<OrderListVO> result = orderService.page(searchDTO);
        return R.success(result);
    }

    @Operation(summary = "获取订单详情", description = "根据订单号获取订单详细信息")
    @GetMapping("/{orderNo}")
    public R<OrderDetailVO> detail(
            @Parameter(description = "订单号", required = true, example = "O202412011234567890")
            @PathVariable String orderNo) {
        OrderDetailVO orderDetail = orderService.getDetailByOrderNo(orderNo);
        return R.success(orderDetail);
    }

    @Operation(summary = "确认收货", description = "确认订单已收货，更新订单状态")
    @PutMapping("/{orderNo}/confirm-receipt")
    public R<Void> confirmReceipt(
            @Parameter(description = "订单号", required = true, example = "O202412011234567890")
            @PathVariable String orderNo) {
        orderService.confirmReceipt(orderNo);
        return R.success();
    }

    @Operation(summary = "审核退款申请", description = "审核客户的退款申请")
    @PutMapping("/{orderNo}/refund-approve")
    public R<Void> approveRefund(
            @Parameter(description = "订单号", required = true, example = "O202412011234567890")
            @PathVariable String orderNo,
            @Parameter(description = "退款审核信息", required = true,
                content = @Content(examples = {
                    @ExampleObject(name = "同意退款", value = """
                        {
                          "refundId": 789,
                          "approved": true,
                          "remark": "同意退款，商品无质量问题",
                          "innerRemark": "客户要求退款，商品已退回"
                        }
                        """),
                    @ExampleObject(name = "拒绝退款", value = """
                        {
                          "refundId": 789,
                          "approved": false,
                          "remark": "商品已使用，不符合退款条件",
                          "innerRemark": "客户已使用商品超过7天"
                        }
                        """)
                }))
            @Valid @RequestBody OrderRefundApprovalDTO approvalDTO) {
        orderService.approveRefund(orderNo, approvalDTO);
        return R.success();
    }

}
