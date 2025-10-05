package com.dobbinsoft.gus.distribution.data.vo.social;

import com.dobbinsoft.gus.distribution.data.enums.UserSrcType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
@Schema(description = "Social Authenticator VO")
public class SocialAuthenticatorVO {

    @Schema(description = "Social Authenticator ID", example = "auth_123456")
    private String id;

    @Schema(description = "Social media source type", example = "WECHAT_MINI")
    private String src;

    @Schema(description = "Social media source type description", example = "微信小程序")
    private String srcDesc;

    @Schema(description = "Application ID", example = "wx1234567890abcdef")
    private String appId;

    @Schema(description = "Application Secret (masked)", example = "******")
    private String appSecret;

    @Schema(description = "Creation time", example = "2024-01-01T00:00:00Z")
    private ZonedDateTime createdTime;

    @Schema(description = "Last modified time", example = "2024-01-01T00:00:00Z")
    private ZonedDateTime modifiedTime;

    @Schema(description = "Created by", example = "admin")
    private String createdBy;

    @Schema(description = "Modified by", example = "admin")
    private String modifiedBy;
} 