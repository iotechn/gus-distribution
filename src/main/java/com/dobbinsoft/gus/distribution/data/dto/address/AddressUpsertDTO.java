package com.dobbinsoft.gus.distribution.data.dto.address;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Data
@Schema(description = "地址创建/更新请求")
public class AddressUpsertDTO {

    @Schema(description = "收件人姓名", example = "张三", required = true)
    @NotBlank(message = "收件人姓名不能为空")
    private String userName;

    @Schema(description = "收件人手机号", example = "13800138000", required = true)
    @NotBlank(message = "收件人手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String telNumber;

    @Schema(description = "邮编", example = "100000")
    private String postalCode;

    @Schema(description = "省份", example = "北京市", required = true)
    @NotBlank(message = "省份不能为空")
    private String provinceName;

    @Schema(description = "城市", example = "北京市", required = true)
    @NotBlank(message = "城市不能为空")
    private String cityName;

    @Schema(description = "县/区", example = "朝阳区", required = true)
    @NotBlank(message = "县/区不能为空")
    private String countyName;

    @Schema(description = "详细地址", example = "三里屯街道1号", required = true)
    @NotBlank(message = "详细地址不能为空")
    private String detailAddress;

    @Schema(description = "是否默认地址", example = "false", defaultValue = "false")
    private Boolean isDefault = false;

    @Schema(description = "地址标签", example = "家", allowableValues = {"家", "公司", "学校", "其他"})
    private String label;

    @Schema(description = "纬度", requiredMode = Schema.RequiredMode.REQUIRED, example = "39.9142")
    private BigDecimal latitude;
    
    @Schema(description = "经度", requiredMode = Schema.RequiredMode.REQUIRED, example = "116.4174")
    private BigDecimal longitude;
} 