package com.dobbinsoft.gus.distribution.client.gus.payment.model;

import lombok.Getter;
import lombok.Setter;

/**
 * 提供商VO类
 * 用于DTO和事件中，避免直接引用实体类
 */
@Getter
@Setter
public class ProviderVO {
    
    private String id;
    
    private String type;
    
    private String config;
    
    private String remark;
    
    private String tenantId;
    
    private Boolean deleted;

}