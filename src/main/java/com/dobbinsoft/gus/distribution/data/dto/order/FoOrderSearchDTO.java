package com.dobbinsoft.gus.distribution.data.dto.order;

import com.dobbinsoft.gus.common.model.dto.PageSearchDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
@Schema(description = "前台订单搜索DTO")
public class FoOrderSearchDTO extends PageSearchDTO {

    @Schema(description = "订单状态", example = "10", 
        allowableValues = {"10", "20", "30", "40", "50", "60", "70", "80", "90"})
    private Integer status;

    @Schema(description = "下单时间开始", example = "2024-12-01T00:00:00Z")
    private ZonedDateTime createTimeStart;

    @Schema(description = "下单时间结束", example = "2024-12-31T23:59:59Z")
    private ZonedDateTime createTimeEnd;

    @Schema(description = "关键字搜索（订单号、商品名称）", example = "iPhone")
    private String keyword;

}

