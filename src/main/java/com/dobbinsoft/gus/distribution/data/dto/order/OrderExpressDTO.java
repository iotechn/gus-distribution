package com.dobbinsoft.gus.distribution.data.dto.order;

import com.dobbinsoft.gus.distribution.data.enums.LpCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "订单发货DTO")
public class OrderExpressDTO {

    @Schema(description = "物流公司代码", example = "SF")
    @NotNull(message = "物流公司代码不能为空")
    private LpCode logisticsCompanyCode;

    @Schema(description = "物流单号", example = "SF1234567890")
    @NotBlank(message = "物流单号不能为空")
    @Size(max = 100, message = "物流单号长度不能超过100个字符")
    private String logisticsNo;

}
