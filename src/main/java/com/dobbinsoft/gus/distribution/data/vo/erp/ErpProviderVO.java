package com.dobbinsoft.gus.distribution.data.vo.erp;

import com.dobbinsoft.gus.distribution.data.enums.ErpProvider;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
public class ErpProviderVO {

    private String id;

    private ErpProvider type;

    private String config;

    private String remark;

    private ZonedDateTime createdTime;

    private String createdBy;

    private ZonedDateTime modifiedTime;

    private String modifiedBy;
}

