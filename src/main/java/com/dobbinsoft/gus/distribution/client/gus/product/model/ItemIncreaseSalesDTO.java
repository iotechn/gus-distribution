package com.dobbinsoft.gus.distribution.client.gus.product.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ItemIncreaseSalesDTO {

    @NotNull(message = "销量数量不能为空")
    @Positive(message = "销量数量必须大于0")
    private BigDecimal quantity;

}

