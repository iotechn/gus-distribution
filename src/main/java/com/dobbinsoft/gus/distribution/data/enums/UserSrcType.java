package com.dobbinsoft.gus.distribution.data.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserSrcType implements BaseEnums<String> {

    DISTRIBUTION_WECHAT_WEB("微信WEB"),
    DISTRIBUTION_PASSWORD("邮箱密码注册"),

    ;

    private final String msg;

    public static UserSrcType getByCode(String code) {
        for (UserSrcType status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    @Override
    public String getCode() {
        return this.name();
    }
}
