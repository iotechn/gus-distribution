package com.dobbinsoft.gus.distribution.data.dto.social;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Social Authenticator Create DTO")
public class SocialAuthenticatorCreateDTO {

    @Schema(description = "Social media source type", example = "WECHAT_MINI",
            allowableValues = {"WECHAT_MINI", "PASSWORD"})
    @NotBlank(message = "Social media source type cannot be empty")
    @Pattern(regexp = "^(WECHAT_MINI|PASSWORD)$", message = "Invalid social media source type")
    private String src;

    @Schema(description = "Application ID", example = "wx1234567890abcdef")
    @NotBlank(message = "Application ID cannot be empty")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "Application ID can only contain letters, numbers, underscores and hyphens")
    private String appId;

    @Schema(description = "Application Secret", example = "abcdef1234567890abcdef1234567890")
    @NotBlank(message = "Application Secret cannot be empty")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "Application Secret can only contain letters, numbers, underscores and hyphens")
    private String appSecret;
} 