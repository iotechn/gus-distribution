package com.dobbinsoft.gus.distribution.client.gus.logistics.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class DeliveryOrderCreateDTO {

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "收件人")
    @NotNull
    private Address receiver;

    @Schema(description = "发件人")
    @NotNull
    private Address sender;

    @Schema(description = "门店Code，冗余方便后期扩展 / 问题排查 / 配送费模板关联门店")
    private String locationCode;

    @Schema(description = "配送单备注")
    private String remark;

    @Schema(description = "是否需要取件码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Boolean enablePickupCode;

    @Schema(description = "是否需要签收码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Boolean enableSignCode;

    @Getter
    @Setter
    public static class Address {

        @Schema(description = "名称", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        private String name;

        @Schema(description = "手机号")
        @NotBlank
        private String mobile;

        @Schema(description = "地址", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        private String address;

        @Schema(description = "经度", example = "116.397128")
        private BigDecimal longitude;

        @Schema(description = "纬度", example = "39.916527")
        private BigDecimal latitude;
    }

}
