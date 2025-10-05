package com.dobbinsoft.gus.distribution.data.po;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName(value = "ds_customizer", autoResultMap = true)
@Schema(description = "客制化实体")
public class CustomizerPO extends BasePO {

    /**
     * com.dobbinsoft.gus.distribution.data.enums.StatusType
     */
    @Schema(description = "状态", example = "1")
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(description = "名称")
    @NotBlank(message = "名称不能为空")
    private String name;

    @Schema(description = "访问次数")
    @NotNull(message = "访问次数不能为空")
    @PositiveOrZero(message = "访问次数必须大于等于0")
    private Integer visitNumber;

    @Schema(description = "访客数量")
    @NotNull(message = "访客数量不能为空")
    @PositiveOrZero(message = "访客数量必须大于等于0")
    private Integer visitorNumber;

    @Schema(description = "页面内容JSON")
    private String content;

}
