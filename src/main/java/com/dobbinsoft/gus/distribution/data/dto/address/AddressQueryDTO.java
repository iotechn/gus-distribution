package com.dobbinsoft.gus.distribution.data.dto.address;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "地址查询请求")
public class AddressQueryDTO {

    @Schema(description = "用户ID", example = "user_123456")
    private String userId;

    @Schema(description = "收件人姓名，支持模糊查询", example = "张三")
    private String userName;

    @Schema(description = "收件人手机号", example = "13800138000")
    private String telNumber;

    @Schema(description = "省份", example = "北京市")
    private String provinceName;

    @Schema(description = "城市", example = "北京市")
    private String cityName;

    @Schema(description = "县/区", example = "朝阳区")
    private String countyName;

    @Schema(description = "是否默认地址", example = "true")
    private Boolean isDefault;

    @Schema(description = "地址标签", example = "家", allowableValues = {"家", "公司", "学校", "其他"})
    private String label;
} 