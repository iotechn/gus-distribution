package com.dobbinsoft.gus.distribution.data.vo.address;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Schema(description = "地址响应对象")
public class AddressVO {

    @Schema(description = "地址ID", example = "addr_123456")
    private String id;

    @Schema(description = "用户ID", example = "user_123456")
    private String userId;

    @Schema(description = "收件人姓名", example = "张三")
    private String userName;

    @Schema(description = "收件人手机号", example = "13800138000")
    private String telNumber;

    @Schema(description = "邮编", example = "100000")
    private String postalCode;

    @Schema(description = "省份", example = "北京市")
    private String provinceName;

    @Schema(description = "城市", example = "北京市")
    private String cityName;

    @Schema(description = "县/区", example = "朝阳区")
    private String countyName;

    @Schema(description = "详细地址", example = "三里屯街道1号")
    private String detailAddress;

    @Schema(description = "是否默认地址", example = "true")
    private Boolean isDefault;

    @Schema(description = "地址标签", example = "家", allowableValues = {"家", "公司", "学校", "其他"})
    private String label;

    @Schema(description = "创建时间", example = "2024-01-01T00:00:00Z")
    private ZonedDateTime createdTime;

    @Schema(description = "修改时间", example = "2024-01-01T00:00:00Z")
    private ZonedDateTime modifiedTime;
} 