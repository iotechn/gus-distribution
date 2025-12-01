package com.dobbinsoft.gus.distribution.data.po;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Getter
@Setter
@Schema(description = "基础实体类")
public class BasePO implements Pk {

    @Schema(description = "主键ID")
    @NotBlank(message = "ID不能为空")
    @TableId
    private String id;

    @Schema(description = "版本号")
    @NotNull(message = "版本号不能为空")
    @Version
    @TableField(value = "version")
    private Long version = 1L;

    @Schema(description = "创建时间")
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private ZonedDateTime createdTime;

    @Schema(description = "创建人")
    @TableField(value = "created_by", fill = FieldFill.INSERT)
    private String createdBy;

    @Schema(description = "修改时间")
    @TableField(value = "modified_time", fill = FieldFill.INSERT_UPDATE)
    private ZonedDateTime modifiedTime;

    @Schema(description = "修改人")
    @TableField(value = "modified_by", fill = FieldFill.INSERT_UPDATE)
    private String modifiedBy;

    @Schema(description = "租户ID")
    @NotBlank(message = "租户ID不能为空")
    @TableField(value = "tenant_id")
    private String tenantId;


    @Override
    public Serializable pk() {
        return this.id;
    }
}
