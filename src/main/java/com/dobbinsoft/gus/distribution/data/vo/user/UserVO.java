package com.dobbinsoft.gus.distribution.data.vo.user;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
public class UserVO {

    private String id;
    /**
     * com.dobbinsoft.gus.distribution.data.enums.StatusType
     */
    private Integer status;

    private String nickname;

    private String avatar;

    /**
     * com.dobbinsoft.gus.distribution.data.enums.GenderType
     */
    private Integer gender;

    private LocalDate birthday;

    private LocalDateTime lastLoginTime;

    private String lastLoginIp;

    private ZonedDateTime createdTime;

    private String createdBy;

    private ZonedDateTime modifiedTime;

    private String modifiedBy;

    private List<Social> socials;

    @Getter
    @Setter
    public static class Social {

        private String socialId;

        private String src;

    }


}
