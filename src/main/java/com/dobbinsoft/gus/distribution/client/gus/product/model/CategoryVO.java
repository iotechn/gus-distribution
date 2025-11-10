package com.dobbinsoft.gus.distribution.client.gus.product.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "Category VO")
public class CategoryVO {
    
    @Schema(description = "Category ID")
    private String id;
    
    @Schema(description = "Category name")
    private String name;
    
    @Schema(description = "Category image URL")
    private String imageUrl;
    
    @Schema(description = "Sort number, smaller number means higher priority")
    private Integer sortNumber;
    
    @Schema(description = "Parent category ID")
    private String parentId;
    
    @Schema(description = "Tenant ID")
    private String tenantId;
    
    @Schema(description = "Child categories list")
    private List<CategoryVO> children;
    
    @Schema(description = "Creation time")
    private LocalDateTime createTime;
    
    @Schema(description = "Update time")
    private LocalDateTime updateTime;
} 