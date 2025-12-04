package com.dobbinsoft.gus.distribution.client.gus.logistics.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Schema(description = "配送费预览请求DTO")
public class DeliveryFeePreviewDTO {

    @Schema(description = "门店Code，为空则使用通用策略", example = "STORE001")
    private String locationCode;

    @Schema(description = "订单金额", requiredMode = Schema.RequiredMode.REQUIRED, example = "25.50")
    @NotNull(message = "订单金额不能为空")
    @DecimalMin(value = "0.01", message = "订单金额必须大于0")
    private BigDecimal orderAmount;

    @Schema(description = "起点纬度", requiredMode = Schema.RequiredMode.REQUIRED, example = "39.9042")
    @NotNull(message = "起点纬度不能为空")
    private BigDecimal startLatitude;

    @Schema(description = "起点经度", requiredMode = Schema.RequiredMode.REQUIRED, example = "116.4074")
    @NotNull(message = "起点经度不能为空")
    private BigDecimal startLongitude;

    @Schema(description = "终点纬度", requiredMode = Schema.RequiredMode.REQUIRED, example = "39.9142")
    @NotNull(message = "终点纬度不能为空")
    private BigDecimal endLatitude;

    @Schema(description = "终点经度", requiredMode = Schema.RequiredMode.REQUIRED, example = "116.4174")
    @NotNull(message = "终点经度不能为空")
    private BigDecimal endLongitude;
}
