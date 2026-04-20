package com.feiyi.feiyiwenhua.service;

import com.feiyi.feiyiwenhua.entity.Comment;
import java.util.List;

public interface CommentService {
    List<Comment> findByPostId(Long postId);

    List<Comment> findTopLevelComments(Long postId);

    List<Comment> findTopLevelComments(Long postId, Long userId);

    List<Comment> findReplies(Long parentId);

    List<Comment> findReplies(Long parentId, Long userId);

    Comment findById(Long id);

    Comment findById(Long id, Long userId);

    Comment save(Comment comment);

    Comment update(Comment comment);

    void deleteById(Long id);

    void incrementLikeCount(Long id);

    com.feiyi.feiyiwenhua.dto.CommentLikeResponse toggleLike(Long id, Long userId);
}