package com.feiyi.feiyiwenhua.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.feiyi.feiyiwenhua.entity.CommentLike;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentLikeRepository extends BaseMapper<CommentLike> {
    default boolean existsByCommentIdAndUserId(Long commentId, Long userId) {
        LambdaQueryWrapper<CommentLike> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CommentLike::getCommentId, commentId)
                .eq(CommentLike::getUserId, userId);
        return selectCount(queryWrapper) > 0;
    }

    default void deleteByCommentIdAndUserId(Long commentId, Long userId) {
        LambdaQueryWrapper<CommentLike> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CommentLike::getCommentId, commentId)
                .eq(CommentLike::getUserId, userId);
        delete(queryWrapper);
    }
}