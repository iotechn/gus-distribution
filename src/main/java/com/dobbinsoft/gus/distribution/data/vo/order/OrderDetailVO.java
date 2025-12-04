package com.dobbinsoft.gus.distribution.data.vo.order;

import com.dobbinsoft.gus.distribution.client.gus.logistics.model.DeliveryOrderVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@Schema(description = "订单详情VO")
public class OrderDetailVO extends OrderListVO {
    
    @Schema(description = "配送单信息")
    private DeliveryOrderVO deliveryOrder;
    
    @Schema(description = "退款信息列表")
    private List<OrderRefundVO> refunds;
    
    @Getter
    @Setter
    @Schema(description = "订单退款VO")
    public static class OrderRefundVO {
        @Schema(description = "退款单ID", example = "refund_123")
        private String id;
        
        @Schema(description = "退款单号", example = "R202412011234567890")
        private String refundNo;
        
        @Schema(description = "订单商品项ID", example = "order_item_123")
        private String orderItemId;
        
        @Schema(description = "退款状态", example = "1")
        private Integer status;
        
        @Schema(description = "退款状态描述", example = "退款中")
        private String statusMsg;
        
        @Schema(description = "退款金额", example = "5999.00")
        private BigDecimal refundAmount;
        
        @Schema(description = "退款原因", example = "商品质量问题")
        private String reason;
        
        @Schema(description = "退款备注", example = "客户要求退款")
        private String remark;
        
        @Schema(description = "内部备注", example = "已核实商品问题")
        private String innerRemark;
        
        @Schema(description = "创建时间", example = "2024-12-01T12:00:00Z")
        private ZonedDateTime createTime;
        
        @Schema(description = "审核时间", example = "2024-12-01T14:00:00Z")
        private ZonedDateTime approvalTime;
    }
}
