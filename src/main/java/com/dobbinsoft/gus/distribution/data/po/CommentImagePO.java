package com.dobbinsoft.gus.distribution.data.po;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@Getter
@Setter
@TableName("ec_comment_image")
@Schema(description = "评论图片实体")
public class CommentImagePO extends BasePO {

    @Schema(description = "评论ID")
    @NotBlank(message = "评论ID不能为空")
    private String commentId;

    @Schema(description = "图片URL")
    @NotBlank(message = "图片URL不能为空")
    private String imageUrl;

    @Schema(description = "图片排序")
    @NotNull(message = "图片排序不能为空")
    @PositiveOrZero(message = "图片排序必须大于等于0")
    private Integer sortOrder;
}
