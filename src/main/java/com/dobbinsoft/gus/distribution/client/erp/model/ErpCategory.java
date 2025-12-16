package com.dobbinsoft.gus.distribution.client.erp.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErpCategory {

    @NotBlank
    private String erpId;

    @NotBlank
    private String name;

    private String imageUrl;

    @NotBlank
    private String erpParentId;

}
