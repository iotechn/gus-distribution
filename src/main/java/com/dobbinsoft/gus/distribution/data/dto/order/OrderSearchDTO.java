package com.dobbinsoft.gus.distribution.data.dto.order;

import com.dobbinsoft.gus.common.model.dto.PageSearchDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
@Schema(description = "订单搜索DTO")
public class OrderSearchDTO extends PageSearchDTO {

    @Schema(description = "订单号", example = "O202412011234567890")
    private String orderNo;

    @Schema(description = "订单状态", example = "10", 
        allowableValues = {"10", "20", "30", "40", "50", "60", "70", "80", "90"})
    private Integer status;

    @Schema(description = "物流公司", example = "顺丰速运")
    private String logisticsCompany;

    @Schema(description = "物流单号", example = "SF1234567890")
    private String logisticsNo;

    @Schema(description = "创建时间开始", example = "2024-12-01T00:00:00Z")
    private ZonedDateTime payTimeStart;

    @Schema(description = "创建时间结束", example = "2024-12-31T23:59:59Z")
    private ZonedDateTime payTimeEnd;

    @Schema(description = "发货时间开始", example = "2024-12-01T00:00:00Z")
    private ZonedDateTime deliveryTimeStart;

    @Schema(description = "发货时间结束", example = "2024-12-31T23:59:59Z")
    private ZonedDateTime deliveryTimeEnd;

}
