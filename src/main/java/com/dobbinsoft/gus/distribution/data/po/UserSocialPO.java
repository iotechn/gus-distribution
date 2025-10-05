package com.dobbinsoft.gus.distribution.data.po;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
@TableName("ds_user_social")
@Schema(description = "用户社交账号关联实体")
public class UserSocialPO extends BasePO{

    @Schema(description = "用户ID")
    @NotBlank(message = "用户ID不能为空")
    private String userId;

    /**
     * com.dobbinsoft.gus.distribution.data.enums.UserSrcType
     */
    @Schema(description = "用户来源类型")
    @NotBlank(message = "用户来源类型不能为空")
    private String src;

    @Schema(description = "社交账号ID")
    @NotBlank(message = "社交账号ID不能为空")
    private String socialId;

}
