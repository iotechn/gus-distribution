package com.dobbinsoft.gus.distribution.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dobbinsoft.gus.distribution.data.dto.comment.CommentCreateDTO;
import com.dobbinsoft.gus.distribution.data.dto.comment.CommentQueryDTO;
import com.dobbinsoft.gus.distribution.data.enums.OrderStatusType;
import com.dobbinsoft.gus.distribution.data.exception.DistributionErrorCode;
import com.dobbinsoft.gus.distribution.data.po.CommentImagePO;
import com.dobbinsoft.gus.distribution.data.po.CommentPO;
import com.dobbinsoft.gus.distribution.data.po.OrderItemPO;
import com.dobbinsoft.gus.distribution.data.po.OrderPO;
import com.dobbinsoft.gus.distribution.data.vo.comment.CommentVO;
import com.dobbinsoft.gus.distribution.mapper.CommentImageMapper;
import com.dobbinsoft.gus.distribution.mapper.CommentMapper;
import com.dobbinsoft.gus.distribution.mapper.OrderItemMapper;
import com.dobbinsoft.gus.distribution.mapper.OrderMapper;
import com.dobbinsoft.gus.distribution.service.CommentService;
import com.dobbinsoft.gus.distribution.utils.SessionUtils;
import com.dobbinsoft.gus.web.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;
    private final CommentImageMapper commentImageMapper;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createComment(CommentCreateDTO commentCreateDTO) {
        // 1. 参数校验
        if (!StringUtils.hasText(commentCreateDTO.getOrderNo())) {
            throw new ServiceException(DistributionErrorCode.COMMENT_PARAM_INVALID);
        }

        // 2. 验证订单是否存在且属于当前用户
        String userId = SessionUtils.getFoSession().getUserId();
        OrderPO order = orderMapper.selectOne(
                new LambdaQueryWrapper<OrderPO>()
                        .eq(OrderPO::getOrderNo, commentCreateDTO.getOrderNo())
                        .eq(OrderPO::getUserId, userId)
        );
        if (order == null) {
            throw new ServiceException(DistributionErrorCode.ORDER_NOT_FOUND);
        }

        // 3. 验证订单状态是否为待评价
        if (!OrderStatusType.WAIT_COMMENT.getCode().equals(order.getStatus())) {
            throw new ServiceException(DistributionErrorCode.ORDER_STATUS_INVALID);
        }

        // 4. 如果没有评论内容，直接完成订单
        if (CollectionUtils.isEmpty(commentCreateDTO.getComments())) {
            order.setStatus(OrderStatusType.COMPLETE.getCode());
            orderMapper.updateById(order);
            return;
        }

        // 5. 处理评论
        for (CommentCreateDTO.CommentItemDTO commentItem : commentCreateDTO.getComments()) {
            // 验证订单项是否存在
            OrderItemPO orderItem = orderItemMapper.selectOne(
                    new LambdaQueryWrapper<OrderItemPO>()
                            .eq(OrderItemPO::getOrderId, order.getId())
                            .eq(OrderItemPO::getSmc, commentItem.getSmc())
                            .eq(OrderItemPO::getSku, commentItem.getSku())
            );
            if (orderItem == null) {
                throw new ServiceException(DistributionErrorCode.COMMENT_ORDER_ITEM_NOT_FOUND);
            }

            // 检查是否已经评论过
            long existingCommentCount = commentMapper.selectCount(
                    new QueryWrapper<CommentPO>()
                            .eq("order_no", commentCreateDTO.getOrderNo())
                            .eq("smc", commentItem.getSmc())
                            .eq("sku", commentItem.getSku())
            );
            if (existingCommentCount > 0) {
                throw new ServiceException(DistributionErrorCode.COMMENT_ALREADY_EXISTS);
            }

            // 创建评论
            CommentPO comment = new CommentPO();
            comment.setSmc(commentItem.getSmc());
            comment.setSku(commentItem.getSku());
            comment.setUserId(userId);
            comment.setOrderNo(commentCreateDTO.getOrderNo());
            comment.setContent(commentItem.getContent());
            comment.setScore(commentItem.getScore());
            comment.setHasImage(!CollectionUtils.isEmpty(commentItem.getImageUrls()));

            commentMapper.insert(comment);

            // 处理评论图片
            if (!CollectionUtils.isEmpty(commentItem.getImageUrls())) {
                for (int i = 0; i < commentItem.getImageUrls().size(); i++) {
                    CommentImagePO commentImage = new CommentImagePO();
                    commentImage.setCommentId(comment.getId());
                    commentImage.setImageUrl(commentItem.getImageUrls().get(i));
                    commentImage.setSortOrder(i + 1);
                    commentImageMapper.insert(commentImage);
                }
            }
        }

        // 6. 完成订单
        order.setStatus(OrderStatusType.COMPLETE.getCode());
        orderMapper.updateById(order);
    }

    @Override
    public List<CommentVO> queryComments(CommentQueryDTO commentQueryDTO) {
        QueryWrapper<CommentPO> queryWrapper = new QueryWrapper<>();
        
        if (StringUtils.hasText(commentQueryDTO.getSmc())) {
            queryWrapper.eq("smc", commentQueryDTO.getSmc());
        }
        if (StringUtils.hasText(commentQueryDTO.getSku())) {
            queryWrapper.eq("sku", commentQueryDTO.getSku());
        }
        if (StringUtils.hasText(commentQueryDTO.getUserId())) {
            queryWrapper.eq("user_id", commentQueryDTO.getUserId());
        }
        if (StringUtils.hasText(commentQueryDTO.getOrderNo())) {
            queryWrapper.eq("order_no", commentQueryDTO.getOrderNo());
        }
        if (commentQueryDTO.getScore() != null) {
            queryWrapper.eq("score", commentQueryDTO.getScore());
        }

        queryWrapper.orderByDesc("created_time");

        List<CommentPO> comments = commentMapper.selectList(queryWrapper);
        return convertToVOList(comments);
    }

    @Override
    public List<CommentVO> getCommentsByOrderNo(String orderNo) {
        QueryWrapper<CommentPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_no", orderNo);
        queryWrapper.orderByDesc("created_time");

        List<CommentPO> comments = commentMapper.selectList(queryWrapper);
        return convertToVOList(comments);
    }

    @Override
    public List<CommentVO> getCommentsByProduct(String smc, String sku) {
        QueryWrapper<CommentPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("smc", smc);
        queryWrapper.eq("sku", sku);
        queryWrapper.orderByDesc("created_time");

        List<CommentPO> comments = commentMapper.selectList(queryWrapper);
        return convertToVOList(comments);
    }

    private List<CommentVO> convertToVOList(List<CommentPO> comments) {
        if (CollectionUtils.isEmpty(comments)) {
            return new ArrayList<>();
        }

        return comments.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    private CommentVO convertToVO(CommentPO comment) {
        CommentVO vo = new CommentVO();
        BeanUtils.copyProperties(comment, vo);

        // 查询评论图片
        QueryWrapper<CommentImagePO> imageQuery = new QueryWrapper<>();
        imageQuery.eq("comment_id", comment.getId());
        imageQuery.orderByAsc("sort_order");
        
        List<CommentImagePO> images = commentImageMapper.selectList(imageQuery);
        List<String> imageUrls = images.stream()
                .map(CommentImagePO::getImageUrl)
                .collect(Collectors.toList());
        vo.setImageUrls(imageUrls);

        return vo;
    }
}
