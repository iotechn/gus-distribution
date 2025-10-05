package com.dobbinsoft.gus.distribution.data.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@Schema(description = "用户状态更新DTO")
public class UserStatusUpdateDTO {

    @Schema(description = "用户ID", required = true, example = "user_123456")
    @NotBlank(message = "用户ID不能为空")
    private String id;

    @Schema(description = "用户状态", required = true, example = "1", 
        allowableValues = {"0", "1"})
    @NotNull(message = "用户状态不能为空")
    private Integer status;

}
