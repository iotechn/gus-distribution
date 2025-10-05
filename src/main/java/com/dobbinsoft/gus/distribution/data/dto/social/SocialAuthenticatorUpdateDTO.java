package com.dobbinsoft.gus.distribution.data.dto.social;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Social Authenticator Update DTO")
public class SocialAuthenticatorUpdateDTO {

    @Schema(description = "Social Authenticator ID", example = "auth_123456", required = true)
    @NotBlank(message = "Social Authenticator ID cannot be empty")
    private String id;

    @Schema(description = "Social media source type", example = "WECHAT_MINI", required = true,
            allowableValues = {"WECHAT_MINI", "PASSWORD"})
    @NotBlank(message = "Social media source type cannot be empty")
    @Pattern(regexp = "^(WECHAT_MINI|PASSWORD)$", message = "Invalid social media source type")
    private String src;

    @Schema(description = "Application ID", example = "wx1234567890abcdef", required = true)
    @NotBlank(message = "Application ID cannot be empty")
    private String appId;

    @Schema(description = "Application Secret", example = "abcdef1234567890abcdef1234567890", required = true)
    @NotBlank(message = "Application Secret cannot be empty")
    private String appSecret;
} 