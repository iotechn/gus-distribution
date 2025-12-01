package com.dobbinsoft.gus.distribution.mapper;

import com.dobbinsoft.gus.distribution.data.po.CommentPO;
import com.dobbinsoft.gus.distribution.mapper.mybatis.MybatisCommentMapper;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper extends MapperAdapter<MybatisCommentMapper, CommentPO> {
}

