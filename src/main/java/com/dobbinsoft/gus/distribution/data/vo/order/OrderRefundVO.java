package com.dobbinsoft.gus.distribution.data.vo.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@Schema(description = "订单退款VO")
public class OrderRefundVO {

    @Schema(description = "退款单ID", example = "refund_123")
    private String id;

    @Schema(description = "退款单号", example = "R202412011234567890")
    private String refundNo;

    @Schema(description = "订单ID", example = "order_123")
    private String orderId;

    @Schema(description = "订单号", example = "O202412011234567890")
    private String orderNo;

    @Schema(description = "退款商品项列表")
    private List<OrderRefundItemVO> refundItems;

    @Schema(description = "用户ID", example = "user_123")
    private String userId;

    @Schema(description = "退款状态", example = "1")
    private Integer status;

    @Schema(description = "退款状态描述", example = "待审核")
    private String statusMsg;

    @Schema(description = "退款金额", example = "5999.00")
    private BigDecimal refundAmount;

    @Schema(description = "退款原因", example = "商品质量问题")
    private String reason;

    @Schema(description = "退款备注", example = "客户要求退款")
    private String remark;

    @Schema(description = "内部备注", example = "已核实商品问题")
    private String innerRemark;

    @Schema(description = "退款凭证图片", example = "[\"https://example.com/image1.jpg\"]")
    private String[] evidenceImages;

    @Schema(description = "审核人ID", example = "admin_123")
    private String approverId;

    @Schema(description = "创建时间", example = "2024-12-01T12:00:00Z")
    private ZonedDateTime createTime;

    @Schema(description = "审核时间", example = "2024-12-01T14:00:00Z")
    private ZonedDateTime approvalTime;

    @Schema(description = "退款处理时间", example = "2024-12-01T15:00:00Z")
    private ZonedDateTime processTime;

    @Schema(description = "退款完成时间", example = "2024-12-01T16:00:00Z")
    private ZonedDateTime completeTime;
}

