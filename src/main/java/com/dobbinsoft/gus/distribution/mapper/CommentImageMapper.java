package com.dobbinsoft.gus.distribution.mapper;

import com.dobbinsoft.gus.distribution.data.po.CommentImagePO;
import com.dobbinsoft.gus.distribution.mapper.mybatis.MybatisCommentImageMapper;
import org.springframework.stereotype.Component;

@Component
public class CommentImageMapper extends MapperAdapter<MybatisCommentImageMapper, CommentImagePO> {
}

