package com.dobbinsoft.gus.distribution.data.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "订单退款审核DTO")
public class OrderRefundApprovalDTO {

    @Schema(description = "退款单ID", example = "refund_123456")
    @NotNull(message = "退款单ID不能为空")
    @NotBlank(message = "退款单ID不能为空")
    private String refundId;

    @Schema(description = "审核结果", example = "true", 
        allowableValues = {"true", "false"})
    @NotNull(message = "审核结果不能为空")
    private Boolean approved;

    @Schema(description = "审核备注", example = "同意退款，商品无质量问题")
    @Size(max = 500, message = "审核备注长度不能超过500个字符")
    private String remark;

    @Schema(description = "内部备注", example = "客户要求退款，商品已退回")
    @Size(max = 500, message = "内部备注长度不能超过500个字符")
    private String innerRemark;

}
