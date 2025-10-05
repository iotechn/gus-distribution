package com.dobbinsoft.gus.distribution.data.dto.comment;

import com.dobbinsoft.gus.distribution.data.dto.PageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "评论查询请求")
public class CommentQueryDTO extends PageDTO {

    @Schema(description = "商品款号")
    private String smc;

    @Schema(description = "SKU")
    private String sku;

    @Schema(description = "用户ID")
    private String userId;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "评分")
    private Integer score;
}
