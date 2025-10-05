package com.dobbinsoft.gus.distribution.data.dto.customizer;

import com.dobbinsoft.gus.common.model.dto.PageSearchDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "自定义页面搜索DTO")
public class CustomizerSearchDTO extends PageSearchDTO {

    @Schema(description = "页面名称，支持模糊查询", example = "首页")
    private String name;

    @Schema(description = "页面状态", example = "1", 
        allowableValues = {"0", "1"})
    private Integer status;
}
