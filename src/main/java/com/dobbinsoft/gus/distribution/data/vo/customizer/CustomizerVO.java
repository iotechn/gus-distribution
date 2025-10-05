package com.dobbinsoft.gus.distribution.data.vo.customizer;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
public class CustomizerVO {

    private String id;

    private Integer status;

    private String name;

    private Integer visitNumber;

    private Integer visitorNumber;

    private String content;

    private ZonedDateTime createdTime;

    private String createdBy;

    private ZonedDateTime modifiedTime;

    private String modifiedBy;

}
