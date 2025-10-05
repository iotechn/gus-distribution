package com.dobbinsoft.gus.distribution.data.dto.user;

import com.dobbinsoft.gus.common.model.dto.PageSearchDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
@Schema(description = "用户搜索DTO")
public class UserSearchDTO extends PageSearchDTO {

    @Schema(description = "用户状态", example = "1", 
        allowableValues = {"0", "1"})
    private Integer status;

    @Schema(description = "性别", example = "1", 
        allowableValues = {"0", "1", "2"})
    private Integer gender;

    @Schema(description = "注册时间开始", example = "2024-12-01T00:00:00Z")
    private ZonedDateTime createTimeStart;

    @Schema(description = "注册时间结束", example = "2024-12-31T23:59:59Z")
    private ZonedDateTime createTimeEnd;

}
