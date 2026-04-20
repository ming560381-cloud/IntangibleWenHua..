package com.feiyi.feiyiwenhua.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.feiyi.feiyiwenhua.entity.Post;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PostRepository extends BaseMapper<Post> {
}