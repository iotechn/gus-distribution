package com.dobbinsoft.gus.distribution.data.po;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Getter
@Setter
@TableName("ds_order_refund")
@Schema(description = "订单退款实体")
public class OrderRefundPO extends BasePO {

    @Schema(description = "退款单号")
    @NotBlank(message = "退款单号不能为空")
    private String refundNo;

    @Schema(description = "订单ID")
    @NotBlank(message = "订单ID不能为空")
    private String orderId;

    @Schema(description = "订单号")
    @NotBlank(message = "订单号不能为空")
    private String orderNo;

    @Schema(description = "订单商品项ID")
    @NotBlank(message = "订单商品项ID不能为空")
    private String orderItemId;

    @Schema(description = "用户ID")
    @NotBlank(message = "用户ID不能为空")
    private String userId;

    @Schema(description = "退款状态")
    @NotNull(message = "退款状态不能为空")
    private Integer status;

    @Schema(description = "退款金额")
    @NotNull(message = "退款金额不能为空")
    private BigDecimal refundAmount;

    @Schema(description = "退款原因")
    @NotBlank(message = "退款原因不能为空")
    private String reason;

    @Schema(description = "退款备注")
    private String remark;

    @Schema(description = "内部备注")
    private String innerRemark;

    @Schema(description = "退款凭证图片")
    private String evidenceImages;

    @Schema(description = "审核人ID")
    private String approverId;

    @Schema(description = "审核时间")
    private ZonedDateTime approvalTime;

    @Schema(description = "退款处理时间")
    private ZonedDateTime processTime;

    @Schema(description = "退款完成时间")
    private ZonedDateTime completeTime;

}
