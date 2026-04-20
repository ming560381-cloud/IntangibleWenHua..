package com.feiyi.feiyiwenhua.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.feiyi.feiyiwenhua.dto.PostLikeResponse;
import com.feiyi.feiyiwenhua.entity.Comment;
import com.feiyi.feiyiwenhua.entity.Post;
import com.feiyi.feiyiwenhua.entity.PostLike;
import com.feiyi.feiyiwenhua.entity.User;
import com.feiyi.feiyiwenhua.repository.CommentRepository;
import com.feiyi.feiyiwenhua.repository.PostLikeRepository;
import com.feiyi.feiyiwenhua.repository.PostRepository;
import com.feiyi.feiyiwenhua.repository.UserRepository;
import com.feiyi.feiyiwenhua.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostLikeRepository postLikeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Override
    public List<Post> findAll() {
        return postRepository.selectList(null);
    }

    @Override
    public List<Post> findPublishedPosts() {
        return findPublishedPosts(null);
    }

    @Override
    public List<Post> findPublishedPosts(Long currentUserId) {
        LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Post::getStatus, 1)
                .orderByDesc(Post::getCreateTime);
        List<Post> posts = postRepository.selectList(queryWrapper);
        fillCommentCount(posts);
        enrichPostsWithAuthorInfo(posts);
        fillLikedState(posts, currentUserId);
        return posts;
    }

    @Override
    public Post findById(Long id) {
        return postRepository.selectById(id);
    }

    @Override
    public Post findById(Long id, Long currentUserId) {
        Post post = postRepository.selectById(id);
        if (post != null) {
            post.setCommentCount(queryCommentCount(post.getId()));
            enrichPostWithAuthorInfo(post);
            post.setLikedByCurrentUser(hasUserLiked(id, currentUserId));
        }
        return post;
    }

    @Override
    public Post save(Post post) {
        Date now = new Date();
        if (post.getId() == null) {
            post.setCreateTime(now);
            post.setUpdateTime(now);
            post.setViewCount(0);
            post.setLikeCount(0);
            post.setCommentCount(0);
            post.setStatus(1);
            postRepository.insert(post);
        } else {
            post.setUpdateTime(now);
            postRepository.updateById(post);
        }
        return post;
    }

    @Override
    public Post update(Post post) {
        post.setUpdateTime(new Date());
        postRepository.updateById(post);
        return post;
    }

    @Override
    public void deleteById(Long id) {
        postRepository.deleteById(id);
    }

    @Override
    public void incrementViewCount(Long id) {
        Post post = postRepository.selectById(id);
        if (post != null) {
            post.setViewCount((post.getViewCount() == null ? 0 : post.getViewCount()) + 1);
            postRepository.updateById(post);
        }
    }

    @Override
    public void incrementLikeCount(Long id) {
        Post post = postRepository.selectById(id);
        if (post != null) {
            post.setLikeCount((post.getLikeCount() == null ? 0 : post.getLikeCount()) + 1);
            postRepository.updateById(post);
        }
    }

    @Override
    public void incrementCommentCount(Long id) {
        Post post = postRepository.selectById(id);
        if (post != null) {
            post.setCommentCount((post.getCommentCount() == null ? 0 : post.getCommentCount()) + 1);
            postRepository.updateById(post);
        }
    }

    @Override
    public void refreshCommentCount(Long postId) {
        if (postId == null) {
            return;
        }
        Post update = new Post();
        update.setId(postId);
        update.setCommentCount(queryCommentCount(postId));
        update.setUpdateTime(new Date());
        postRepository.updateById(update);
    }

    @Override
    @Transactional
    public PostLikeResponse toggleLike(Long postId, Long userId) {
        LambdaQueryWrapper<PostLike> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PostLike::getPostId, postId)
                .eq(PostLike::getUserId, userId)
                .last("LIMIT 1");

        PostLike existingLike = postLikeRepository.selectOne(queryWrapper);
        boolean liked;

        if (existingLike != null) {
            postLikeRepository.deleteById(existingLike.getId());
            liked = false;
        } else {
            PostLike postLike = new PostLike();
            postLike.setPostId(postId);
            postLike.setUserId(userId);
            postLike.setCreateTime(new Date());
            postLikeRepository.insert(postLike);
            liked = true;
        }

        int likeCount = refreshLikeCount(postId);
        return new PostLikeResponse(postId, likeCount, liked);
    }

    private void fillLikedState(List<Post> posts, Long currentUserId) {
        if (posts == null || posts.isEmpty()) {
            return;
        }

        Set<Long> likedPostIds = Collections.emptySet();
        if (currentUserId != null) {
            List<Long> postIds = posts.stream()
                    .map(Post::getId)
                    .collect(Collectors.toList());

            LambdaQueryWrapper<PostLike> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(PostLike::getUserId, currentUserId)
                    .in(PostLike::getPostId, postIds);

            likedPostIds = postLikeRepository.selectList(queryWrapper).stream()
                    .map(PostLike::getPostId)
                    .collect(Collectors.toSet());
        }

        for (Post post : posts) {
            post.setLikedByCurrentUser(likedPostIds.contains(post.getId()));
        }
    }

    private boolean hasUserLiked(Long postId, Long currentUserId) {
        if (postId == null || currentUserId == null) {
            return false;
        }

        LambdaQueryWrapper<PostLike> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PostLike::getPostId, postId)
                .eq(PostLike::getUserId, currentUserId);
        return postLikeRepository.selectCount(queryWrapper) > 0;
    }

    private int refreshLikeCount(Long postId) {
        LambdaQueryWrapper<PostLike> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PostLike::getPostId, postId);
        int likeCount = Math.toIntExact(postLikeRepository.selectCount(queryWrapper));

        Post update = new Post();
        update.setId(postId);
        update.setLikeCount(likeCount);
        update.setUpdateTime(new Date());
        postRepository.updateById(update);

        return likeCount;
    }

    private void fillCommentCount(List<Post> posts) {
        if (posts == null || posts.isEmpty()) {
            return;
        }
        for (Post post : posts) {
            post.setCommentCount(queryCommentCount(post.getId()));
        }
    }

    private int queryCommentCount(Long postId) {
        if (postId == null) {
            return 0;
        }
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getPostId, postId)
                .eq(Comment::getStatus, 0);
        return Math.toIntExact(commentRepository.selectCount(queryWrapper));
    }

    private void enrichPostsWithAuthorInfo(List<Post> posts) {
        if (posts == null || posts.isEmpty())
            return;
        List<Long> authorIds = posts.stream()
                .map(Post::getAuthorId)
                .distinct()
                .collect(Collectors.toList());
        if (authorIds.isEmpty())
            return;

        LambdaQueryWrapper<User> uq = new LambdaQueryWrapper<>();
        uq.in(User::getId, authorIds);
        Map<Long, User> userMap = userRepository.selectList(uq).stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        for (Post post : posts) {
            User author = userMap.get(post.getAuthorId());
            if (author != null) {
                post.setAuthorAvatar(author.getAvatar());
                post.setAuthorInitial(author.getInitial());
            } else {
                String name = post.getAuthorName();
                post.setAuthorInitial(name != null && !name.isEmpty()
                        ? name.substring(0, 1).toUpperCase()
                        : "?");
            }
        }
    }

    private void enrichPostWithAuthorInfo(Post post) {
        if (post == null || post.getAuthorId() == null)
            return;
        User author = userRepository.selectById(post.getAuthorId());
        if (author != null) {
            post.setAuthorAvatar(author.getAvatar());
            post.setAuthorInitial(author.getInitial());
        } else {
            String name = post.getAuthorName();
            post.setAuthorInitial(name != null && !name.isEmpty()
                    ? name.substring(0, 1).toUpperCase()
                    : "?");
        }
    }
}
