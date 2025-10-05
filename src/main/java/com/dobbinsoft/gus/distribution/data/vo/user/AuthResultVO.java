package com.dobbinsoft.gus.distribution.data.vo.user;

import lombok.Data;

@Data
public class AuthResultVO {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Integer expiresIn;
    private Integer refreshExpiresIn;
}
