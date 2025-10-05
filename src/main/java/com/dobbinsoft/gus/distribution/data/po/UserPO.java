package com.dobbinsoft.gus.distribution.data.po;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@TableName("ec_user")
@Schema(description = "用户实体")
public class UserPO extends BasePO {

    /**
     * com.dobbinsoft.gus.distribution.data.enums.StatusType
     */
    @Schema(description = "用户状态", example = "1")
    @NotNull(message = "用户状态不能为空")
    private Integer status;

    @Schema(description = "用户昵称")
    @NotBlank(message = "用户昵称不能为空")
    private String nickname;

    @Schema(description = "用户头像")
    private String avatar;

    /**
     * com.dobbinsoft.gus.distribution.data.enums.GenderType
     */
    @Schema(description = "性别", example = "1")
    private Integer gender;

    @Schema(description = "生日")
    private LocalDate birthday;

    @Schema(description = "最后登录时间")
    private LocalDateTime lastLoginTime;

    @Schema(description = "最后登录IP")
    private String lastLoginIp;

}
