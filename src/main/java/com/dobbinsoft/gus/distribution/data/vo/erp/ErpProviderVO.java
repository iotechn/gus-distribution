package com.dobbinsoft.gus.distribution.data.vo.erp;

import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErpProviderVO {

    private String id;

    private String type;

    private String config;

    private String remark;

    private ZonedDateTime createdTime;

    private String createdBy;

    private ZonedDateTime modifiedTime;

    private String modifiedBy;
}

