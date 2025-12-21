package com.dobbinsoft.gus.distribution.data.dto.erp;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "ERP提供商创建DTO")
public class ErpProviderCreateDTO {

    @Schema(description = "ERP提供商类型", example = "JDY")
    @NotNull(message = "ERP提供商类型不能为空")
    private String type;

    @Schema(description = "配置信息（JSON格式，需要AES加密）",  example = "{\"key\":\"xxx\",\"secret\":\"xxx\"}")
    @NotBlank(message = "配置信息不能为空")
    private String config;

    @Schema(description = "备注", example = "简道云ERP配置")
    private String remark;
}

