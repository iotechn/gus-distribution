package com.dobbinsoft.gus.distribution.controller.fo;

import com.dobbinsoft.gus.distribution.data.vo.order.OrderDetailVO;
import com.dobbinsoft.gus.web.vo.R;
import com.dobbinsoft.gus.distribution.data.dto.order.OrderPrepayDTO;
import com.dobbinsoft.gus.distribution.data.dto.order.OrderRefundApplyDTO;
import com.dobbinsoft.gus.distribution.data.dto.order.OrderSubmitDTO;
import com.dobbinsoft.gus.distribution.data.vo.order.OrderPreviewVO;
import com.dobbinsoft.gus.distribution.data.vo.order.OrderPrepayVO;
import com.dobbinsoft.gus.distribution.data.vo.order.OrderRefundVO;
import com.dobbinsoft.gus.distribution.data.vo.order.OrderVO;
import com.dobbinsoft.gus.distribution.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fo/order")
@RequiredArgsConstructor
@Tag(name = "前台订单管理", description = "前台订单相关接口")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/preview")
    @Operation(summary = "订单预览", description = "预览订单信息，包括商品总价、折扣金额、物流费用等")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "预览成功", 
            content = @Content(schema = @Schema(implementation = OrderPreviewVO.class))),
        @ApiResponse(responseCode = "400", description = "参数错误"),
        @ApiResponse(responseCode = "401", description = "用户未登录")
    })
    public R<OrderPreviewVO> preview(
            @Parameter(description = "订单提交信息", required = true)
            @Valid @RequestBody OrderSubmitDTO submitDTO) {
        OrderPreviewVO previewVO = orderService.preview(submitDTO);
        return R.success(previewVO);
    }

    @PostMapping("/submit")
    @Operation(summary = "提交订单", description = "提交订单，创建订单记录并扣减库存")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "提交成功", 
            content = @Content(schema = @Schema(implementation = OrderVO.class))),
        @ApiResponse(responseCode = "400", description = "参数错误"),
        @ApiResponse(responseCode = "401", description = "用户未登录"),
        @ApiResponse(responseCode = "500", description = "库存不足或系统错误")
    })
    public R<OrderVO> submit(
            @Parameter(description = "订单提交信息", required = true)
            @Valid @RequestBody OrderSubmitDTO submitDTO) {
        OrderVO orderVO = orderService.submit(submitDTO);
        return R.success(orderVO);
    }

    @PostMapping("/prepay")
    @Operation(summary = "订单预支付", description = "创建预支付订单，返回支付参数")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "预支付成功", 
            content = @Content(schema = @Schema(implementation = OrderPrepayVO.class))),
        @ApiResponse(responseCode = "400", description = "参数错误"),
        @ApiResponse(responseCode = "401", description = "用户未登录"),
        @ApiResponse(responseCode = "403", description = "无权限操作此订单"),
        @ApiResponse(responseCode = "404", description = "订单不存在"),
        @ApiResponse(responseCode = "500", description = "系统错误")
    })
    public R<OrderPrepayVO> prepay(
            @Parameter(description = "预支付请求信息", required = true)
            @Valid @RequestBody OrderPrepayDTO prepayDTO) {
        OrderPrepayVO prepayVO = orderService.prepay(prepayDTO);
        return R.success(prepayVO);
    }

    @GetMapping("/{orderNo}")
    @Operation(summary = "根据订单号获取订单详情", description = "根据订单号获取订单详细信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功", 
            content = @Content(schema = @Schema(implementation = OrderVO.class))),
        @ApiResponse(responseCode = "401", description = "用户未登录"),
        @ApiResponse(responseCode = "403", description = "无权限查看此订单"),
        @ApiResponse(responseCode = "404", description = "订单不存在")
    })
    public R<OrderDetailVO> getByOrderNo(
            @Parameter(description = "订单号", required = true, example = "O202412011234567890")
            @PathVariable String orderNo) {
        OrderDetailVO orderDetailVO = orderService.getByOrderNo(orderNo);
        return R.success(orderDetailVO);
    }

    @PostMapping("/refund/apply")
    @Operation(summary = "申请退款", description = "用户申请订单退款")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "申请成功", 
            content = @Content(schema = @Schema(implementation = OrderRefundVO.class))),
        @ApiResponse(responseCode = "400", description = "参数错误"),
        @ApiResponse(responseCode = "401", description = "用户未登录"),
        @ApiResponse(responseCode = "403", description = "无权限操作此订单"),
        @ApiResponse(responseCode = "404", description = "订单不存在")
    })
    public R<OrderRefundVO> applyRefund(
            @Parameter(description = "退款申请信息", required = true)
            @Valid @RequestBody OrderRefundApplyDTO applyDTO) {
        OrderRefundVO refundVO = orderService.applyRefund(applyDTO);
        return R.success(refundVO);
    }

    @GetMapping("/refund/list")
    @Operation(summary = "获取退款列表", description = "获取用户的退款申请列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功", 
            content = @Content(schema = @Schema(implementation = OrderRefundVO.class))),
        @ApiResponse(responseCode = "401", description = "用户未登录")
    })
    public R<List<OrderRefundVO>> getRefunds(
            @Parameter(description = "订单号（可选）", example = "O202412011234567890")
            @RequestParam(required = false) String orderNo) {
        List<OrderRefundVO> refunds = orderService.getUserRefunds(orderNo);
        return R.success(refunds);
    }

    @PostMapping("/refund/{refundId}/cancel")
    @Operation(summary = "取消退款申请", description = "用户取消退款申请")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "取消成功"),
        @ApiResponse(responseCode = "400", description = "参数错误"),
        @ApiResponse(responseCode = "401", description = "用户未登录"),
        @ApiResponse(responseCode = "403", description = "无权限操作此退款"),
        @ApiResponse(responseCode = "404", description = "退款记录不存在")
    })
    public R<Void> cancelRefund(
            @Parameter(description = "退款单ID", required = true, example = "refund_123456")
            @PathVariable String refundId) {
        orderService.cancelRefund(refundId);
        return R.success();
    }
}
