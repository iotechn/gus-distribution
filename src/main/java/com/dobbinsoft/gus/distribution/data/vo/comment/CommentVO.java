package com.dobbinsoft.gus.distribution.data.vo.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@Schema(description = "评论信息")
public class CommentVO {

    @Schema(description = "评论ID")
    private String id;

    @Schema(description = "商品款号")
    private String smc;

    @Schema(description = "SKU")
    private String sku;

    @Schema(description = "用户ID")
    private String userId;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "评论内容")
    private String content;

    @Schema(description = "评分")
    private Integer score;

    @Schema(description = "是否有图片")
    private Boolean hasImage;

    @Schema(description = "图片URL列表")
    private List<String> imageUrls;

    @Schema(description = "创建时间")
    private ZonedDateTime createdTime;

    @Schema(description = "修改时间")
    private ZonedDateTime modifiedTime;
}
