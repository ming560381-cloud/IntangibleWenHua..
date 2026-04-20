package com.feiyi.feiyiwenhua.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.feiyi.feiyiwenhua.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentRepository extends BaseMapper<Comment> {
}