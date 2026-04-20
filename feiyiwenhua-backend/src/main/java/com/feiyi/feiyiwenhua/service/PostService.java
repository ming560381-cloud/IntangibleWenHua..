package com.feiyi.feiyiwenhua.service;

import com.feiyi.feiyiwenhua.dto.PostLikeResponse;
import com.feiyi.feiyiwenhua.entity.Post;
import java.util.List;

public interface PostService {
    List<Post> findAll();
    List<Post> findPublishedPosts();
    List<Post> findPublishedPosts(Long currentUserId);
    Post findById(Long id);
    Post findById(Long id, Long currentUserId);
    Post save(Post post);
    Post update(Post post);
    void deleteById(Long id);
    void incrementViewCount(Long id);
    void incrementLikeCount(Long id);
    void incrementCommentCount(Long id);
    void refreshCommentCount(Long postId);
    PostLikeResponse toggleLike(Long postId, Long userId);
}
