package com.dobbinsoft.gus.distribution.data.dto.erp;

import com.dobbinsoft.gus.distribution.data.enums.ErpProvider;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "ERP提供商更新DTO")
public class ErpProviderUpdateDTO {

    @Schema(description = "ERP提供商类型", example = "JDY")
    private ErpProvider type;

    @Schema(description = "配置信息（JSON格式，需要AES加密）", example = "{\"key\":\"xxx\",\"secret\":\"xxx\"}")
    private String config;

    @Schema(description = "备注", example = "简道云ERP配置")
    private String remark;
}

