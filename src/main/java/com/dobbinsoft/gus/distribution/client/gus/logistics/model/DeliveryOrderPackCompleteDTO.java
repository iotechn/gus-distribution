package com.dobbinsoft.gus.distribution.client.gus.logistics.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class DeliveryOrderPackCompleteDTO {

    @Schema(description = "订单号（与deliveryNo二选一）")
    private String orderNo;

    @Schema(description = "配送单号（与orderNo二选一）")
    private String deliveryNo;


}
