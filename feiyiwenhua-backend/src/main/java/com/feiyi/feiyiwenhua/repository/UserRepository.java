package com.feiyi.feiyiwenhua.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.feiyi.feiyiwenhua.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserRepository extends BaseMapper<User> {
}