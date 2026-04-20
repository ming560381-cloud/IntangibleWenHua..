package com.feiyi.feiyiwenhua.controller.api;

import com.feiyi.feiyiwenhua.entity.Comment;
import com.feiyi.feiyiwenhua.service.CommentService;
import com.feiyi.feiyiwenhua.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/community/comments")
public class CommentApiController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private PostService postService;

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<Comment>> getCommentsByPost(@PathVariable Long postId, HttpServletRequest request) {
        Long userId = currentUserId(request);
        return ResponseEntity.ok(commentService.findTopLevelComments(postId, userId));
    }

    @GetMapping("/{id}/replies")
    public ResponseEntity<List<Comment>> getReplies(@PathVariable Long id, HttpServletRequest request) {
        Long userId = currentUserId(request);
        return ResponseEntity.ok(commentService.findReplies(id, userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Comment> detail(@PathVariable Long id, HttpServletRequest request) {
        Long userId = currentUserId(request);
        Comment comment = commentService.findById(id, userId);
        if (comment == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(comment);
    }

    @PostMapping
    public ResponseEntity<Comment> create(@RequestBody Comment comment) {
        if (comment.getPostId() == null || comment.getContent() == null || comment.getUserId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        Comment saved = commentService.save(comment);
        // 增加帖子的评论数
        postService.incrementCommentCount(comment.getPostId());
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Comment> update(@PathVariable Long id, @RequestBody Comment comment) {
        Comment existingComment = commentService.findById(id);
        if (existingComment == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        comment.setId(id);
        return ResponseEntity.ok(commentService.update(comment));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Comment existingComment = commentService.findById(id);
        if (existingComment == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        commentService.deleteById(id);
        postService.refreshCommentCount(existingComment.getPostId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<com.feiyi.feiyiwenhua.dto.CommentLikeResponse> like(@PathVariable Long id,
            HttpServletRequest request) {
        Comment existingComment = commentService.findById(id);
        if (existingComment == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Long userId = currentUserId(request);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(commentService.toggleLike(id, userId));
    }

    @DeleteMapping("/{id}/like")
    public ResponseEntity<com.feiyi.feiyiwenhua.dto.CommentLikeResponse> unlike(@PathVariable Long id,
            HttpServletRequest request) {
        Comment existingComment = commentService.findById(id);
        if (existingComment == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Long userId = currentUserId(request);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(commentService.toggleLike(id, userId));
    }

    private Long currentUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }

        Object userId = session.getAttribute("userId");
        if (userId instanceof Long) {
            return (Long) userId;
        }
        if (userId instanceof Integer) {
            return ((Integer) userId).longValue();
        }
        return null;
    }
}