package com.dobbinsoft.gus.distribution.data.dto.customizer;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "自定义页面创建DTO")
public class CustomizerCreateDTO {

    @Schema(description = "页面名称", required = true, example = "首页自定义")
    @NotBlank(message = "页面名称不能为空")
    private String name;

    @Schema(description = "页面状态", required = true, example = "1", 
        allowableValues = {"0", "1"})
    @NotNull(message = "页面状态不能为空")
    private Integer status;

    @Schema(description = "页面内容JSON", required = true, example = "{\"components\": []}")
    @NotBlank(message = "页面内容不能为空")
    private String content;
}
