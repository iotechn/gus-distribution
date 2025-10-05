package com.dobbinsoft.gus.distribution.data.dto.customizer;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "自定义页面更新DTO")
public class CustomizerUpdateDTO {

    @Schema(description = "页面名称", required = true, example = "首页自定义")
    private String name;

    @Schema(description = "页面状态", required = true, example = "1", 
        allowableValues = {"0", "1"})
    private Integer status;

    @Schema(description = "页面内容JSON", required = true, example = "{\"components\": []}")
    private String content;
}
