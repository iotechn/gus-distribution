package com.dobbinsoft.gus.distribution.data.dto.email;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailTemplateParamDTO {

    @Schema(description = "参数名")
    private String name;

    @Schema(description = "参数值")
    private String value;

} 