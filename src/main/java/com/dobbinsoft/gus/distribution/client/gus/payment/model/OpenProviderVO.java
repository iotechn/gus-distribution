package com.dobbinsoft.gus.distribution.client.gus.payment.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * 开放API - 提供商响应VO
 */
@Getter
@Setter
public class OpenProviderVO {

    @Schema(description = "提供商ID", example = "uuid-generated-id")
    private String id;

    @Schema(description = "提供商类型", example = "WECHAT_JS")
    private String type;

    @Schema(description = "备注", example = "微信支付配置")
    private String remark;

    @Schema(description = "租户ID", example = "tenant-123")
    private String tenantId;
}

