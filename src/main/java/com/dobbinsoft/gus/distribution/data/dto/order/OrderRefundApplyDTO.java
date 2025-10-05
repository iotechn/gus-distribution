package com.dobbinsoft.gus.distribution.data.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "订单退款申请DTO")
public class OrderRefundApplyDTO {

    @Schema(description = "订单号", example = "O202412011234567890")
    @NotBlank(message = "订单号不能为空")
    private String orderNo;

    @Schema(description = "退款原因", example = "商品质量问题")
    @NotBlank(message = "退款原因不能为空")
    @Size(max = 200, message = "退款原因长度不能超过200个字符")
    private String reason;
    
    @Schema(description = "退款商品项列表")
    @NotEmpty(message = "退款商品项不能为空")
    @Valid
    private List<OrderRefundItemDTO> orderRefundItems;

    @Schema(description = "退款备注", example = "商品存在质量问题，要求退款")
    @Size(max = 500, message = "退款备注长度不能超过500个字符")
    private String remark;

    @Schema(description = "退款凭证图片URLs", example = "[\"https://example.com/image1.jpg\", \"https://example.com/image2.jpg\"]")
    private String[] evidenceImages;
}
