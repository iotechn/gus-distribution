package com.dobbinsoft.gus.distribution.data.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dobbinsoft.gus.distribution.data.vo.address.AddressVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@TableName("ds_address")
@Schema(description = "地址实体")
public class AddressPO extends BasePO {

    @Schema(description = "用户ID")
    @NotBlank(message = "用户ID不能为空")
    private String userId;

    @Schema(description = "收件人姓名")
    @NotBlank(message = "收件人姓名不能为空")
    private String userName;

    @Schema(description = "收件人手机号")
    @NotBlank(message = "收件人手机号不能为空")
    private String telNumber;

    @Schema(description = "邮编")
    private String postalCode;

    @Schema(description = "省份")
    @NotBlank(message = "省份不能为空")
    private String provinceName;

    @Schema(description = "城市")
    @NotBlank(message = "城市不能为空")
    private String cityName;

    @Schema(description = "县/区")
    @NotBlank(message = "县/区不能为空")
    private String countyName;

    @Schema(description = "详细地址")
    @NotBlank(message = "详细地址不能为空")
    private String detailAddress;

    @Schema(description = "是否默认地址")
    @NotNull(message = "是否默认地址不能为空")
    private Boolean isDefault;

    @Schema(description = "地址标签（家、公司等）")
    private String label;

    public AddressVO convertToVO() {
        AddressVO addressVO = new AddressVO();
        BeanUtils.copyProperties(this, addressVO);
        return addressVO;
    }
} 