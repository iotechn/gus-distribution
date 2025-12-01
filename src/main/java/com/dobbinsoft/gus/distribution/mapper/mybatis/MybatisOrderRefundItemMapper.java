package com.dobbinsoft.gus.distribution.mapper.mybatis;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dobbinsoft.gus.distribution.data.po.OrderRefundItemPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单退款项Mapper
 */
@Mapper
public interface MybatisOrderRefundItemMapper extends BaseMapper<OrderRefundItemPO> {
}