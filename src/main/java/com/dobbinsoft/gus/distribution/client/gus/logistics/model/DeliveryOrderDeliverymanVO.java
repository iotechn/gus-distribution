package com.dobbinsoft.gus.distribution.client.gus.logistics.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "配送单中的配送员信息VO")
public class DeliveryOrderDeliverymanVO {

    @Schema(description = "配送员ID")
    private String id;

    @Schema(description = "配送员姓名")
    private String name;

    @Schema(description = "配送员用户名")
    private String username;

    @Schema(description = "配送员邮箱")
    private String employeeEmail;

    @Schema(description = "允许接单的位置Code列表")
    private List<String> allowedLocationCodes;

}
