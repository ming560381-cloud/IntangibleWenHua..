package com.feiyi.feiyiwenhua.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.feiyi.feiyiwenhua.entity.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryRepository extends BaseMapper<Category> {
}