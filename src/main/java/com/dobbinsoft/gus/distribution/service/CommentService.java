package com.dobbinsoft.gus.distribution.service;

import com.dobbinsoft.gus.distribution.data.dto.comment.CommentCreateDTO;
import com.dobbinsoft.gus.distribution.data.dto.comment.CommentQueryDTO;
import com.dobbinsoft.gus.distribution.data.vo.comment.CommentVO;

import java.util.List;

public interface CommentService {

    /**
     * 创建评论
     *
     * @param commentCreateDTO 评论创建请求
     */
    void createComment(CommentCreateDTO commentCreateDTO);

    /**
     * 查询评论列表
     *
     * @param commentQueryDTO 查询条件
     * @return 评论列表
     */
    List<CommentVO> queryComments(CommentQueryDTO commentQueryDTO);

    /**
     * 根据订单号查询评论
     *
     * @param orderNo 订单号
     * @return 评论列表
     */
    List<CommentVO> getCommentsByOrderNo(String orderNo);

    /**
     * 根据商品查询评论
     *
     * @param smc 商品款号
     * @param sku SKU
     * @return 评论列表
     */
    List<CommentVO> getCommentsByProduct(String smc, String sku);
}
