package com.dobbinsoft.gus.distribution.data.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dobbinsoft.gus.distribution.data.handler.AesTypeHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
@TableName(value = "ec_social_authenticator", autoResultMap = true)
@Schema(description = "社交认证器实体")
public class SocialAuthenticatorPO extends BasePO {

    /**
     * com.dobbinsoft.gus.distribution.data.enums.UserSrcType
     */
    @Schema(description = "用户来源类型")
    @NotBlank(message = "用户来源类型不能为空")
    private String src;

    @Schema(description = "应用ID")
    @NotBlank(message = "应用ID不能为空")
    private String appId;

    @Schema(description = "应用密钥")
    @NotBlank(message = "应用密钥不能为空")
    @TableField(typeHandler = AesTypeHandler.class)
    private String appSecret;

}
