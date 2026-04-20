package com.feiyi.feiyiwenhua.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.feiyi.feiyiwenhua.entity.Comment;
import com.feiyi.feiyiwenhua.entity.CommentLike;
import com.feiyi.feiyiwenhua.entity.User;
import com.feiyi.feiyiwenhua.repository.CommentLikeRepository;
import com.feiyi.feiyiwenhua.repository.CommentRepository;
import com.feiyi.feiyiwenhua.repository.UserRepository;
import com.feiyi.feiyiwenhua.service.CommentService;
import com.feiyi.feiyiwenhua.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentLikeRepository commentLikeRepository;

    @Override
    public List<Comment> findByPostId(Long postId) {
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getPostId, postId)
                .orderByAsc(Comment::getCreateTime);
        List<Comment> comments = commentRepository.selectList(queryWrapper);
        enrichCommentsWithUserInfo(comments);
        return comments;
    }

    @Override
    public List<Comment> findTopLevelComments(Long postId) {
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getPostId, postId)
                .eq(Comment::getParentId, 0)
                .orderByAsc(Comment::getCreateTime);
        List<Comment> comments = commentRepository.selectList(queryWrapper);
        enrichCommentsWithUserInfo(comments);
        return comments;
    }

    @Override
    public List<Comment> findReplies(Long parentId) {
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getParentId, parentId)
                .orderByAsc(Comment::getCreateTime);
        List<Comment> comments = commentRepository.selectList(queryWrapper);
        enrichCommentsWithUserInfo(comments);
        return comments;
    }

    @Override
    public Comment findById(Long id) {
        Comment comment = commentRepository.selectById(id);
        if (comment != null) {
            enrichCommentWithUserInfo(comment);
        }
        return comment;
    }

    @Override
    public Comment save(Comment comment) {
        Date now = new Date();
        if (comment.getId() == null) {
            comment.setCreateTime(now);
            comment.setUpdateTime(now);
            comment.setLikeCount(0);
            comment.setStatus(0);
            if (comment.getParentId() == null) {
                comment.setParentId(0L);
            }
            commentRepository.insert(comment);
        } else {
            comment.setUpdateTime(now);
            commentRepository.updateById(comment);
        }
        enrichCommentWithUserInfo(comment);
        return comment;
    }

    @Override
    public Comment update(Comment comment) {
        comment.setUpdateTime(new Date());
        commentRepository.updateById(comment);
        enrichCommentWithUserInfo(comment);
        return comment;
    }

    @Override
    public void deleteById(Long id) {
        commentRepository.deleteById(id);
    }

    @Override
    public void incrementLikeCount(Long id) {
        Comment comment = commentRepository.selectById(id);
        if (comment != null) {
            comment.setLikeCount((comment.getLikeCount() == null ? 0 : comment.getLikeCount()) + 1);
            commentRepository.updateById(comment);
        }
    }

    @Override
    public com.feiyi.feiyiwenhua.dto.CommentLikeResponse toggleLike(Long id, Long userId) {
        // 检查是否已经点赞
        boolean exists = commentLikeRepository.existsByCommentIdAndUserId(id, userId);
        Comment comment = commentRepository.selectById(id);
        boolean liked = false;
        int likeCount = 0;
        if (comment != null) {
            int currentLikeCount = comment.getLikeCount() == null ? 0 : comment.getLikeCount();
            if (exists) {
                // 取消点赞
                commentLikeRepository.deleteByCommentIdAndUserId(id, userId);
                likeCount = Math.max(0, currentLikeCount - 1);
                comment.setLikeCount(likeCount);
            } else {
                // 添加点赞
                CommentLike commentLike = new CommentLike();
                commentLike.setCommentId(id);
                commentLike.setUserId(userId);
                commentLike.setCreateTime(new Date());
                commentLikeRepository.insert(commentLike);
                likeCount = currentLikeCount + 1;
                comment.setLikeCount(likeCount);
                liked = true;
            }
            commentRepository.updateById(comment);
        }
        return new com.feiyi.feiyiwenhua.dto.CommentLikeResponse(id, likeCount, liked);
    }

    @Override
    public List<Comment> findTopLevelComments(Long postId, Long userId) {
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getPostId, postId)
                .eq(Comment::getParentId, 0)
                .orderByAsc(Comment::getCreateTime);
        List<Comment> comments = commentRepository.selectList(queryWrapper);
        enrichCommentsWithUserInfo(comments);
        if (userId != null) {
            setCommentLikeStatus(comments, userId);
        }
        return comments;
    }

    @Override
    public List<Comment> findReplies(Long parentId, Long userId) {
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getParentId, parentId)
                .orderByAsc(Comment::getCreateTime);
        List<Comment> comments = commentRepository.selectList(queryWrapper);
        enrichCommentsWithUserInfo(comments);
        if (userId != null) {
            setCommentLikeStatus(comments, userId);
        }
        return comments;
    }

    @Override
    public Comment findById(Long id, Long userId) {
        Comment comment = commentRepository.selectById(id);
        if (comment != null) {
            enrichCommentWithUserInfo(comment);
            if (userId != null) {
                comment.setIsLiked(commentLikeRepository.existsByCommentIdAndUserId(id, userId));
            }
        }
        return comment;
    }

    private void setCommentLikeStatus(List<Comment> comments, Long userId) {
        for (Comment comment : comments) {
            comment.setIsLiked(commentLikeRepository.existsByCommentIdAndUserId(comment.getId(), userId));
        }
    }

    private void enrichCommentsWithUserInfo(List<Comment> comments) {
        if (comments == null || comments.isEmpty()) {
            return;
        }

        List<Long> userIds = comments.stream()
                .map(Comment::getUserId)
                .distinct()
                .collect(Collectors.toList());

        if (userIds.isEmpty()) {
            return;
        }

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(User::getId, userIds);
        List<User> users = userRepository.selectList(queryWrapper);

        Map<Long, User> userMap = users.stream()
                .collect(Collectors.toMap(User::getId, user -> user));

        for (Comment comment : comments) {
            User user = userMap.get(comment.getUserId());
            if (user != null) {
                comment.setUserAvatar(user.getAvatar());
                comment.setUserInitial(user.getInitial());
            } else {
                comment.setUserInitial(comment.getUserInitial());
            }
        }
    }

    private void enrichCommentWithUserInfo(Comment comment) {
        if (comment == null) {
            return;
        }

        User user = userRepository.selectById(comment.getUserId());
        if (user != null) {
            comment.setUserAvatar(user.getAvatar());
            comment.setUserInitial(user.getInitial());
        } else {
            comment.setUserInitial(comment.getUserInitial());
        }
    }
}
