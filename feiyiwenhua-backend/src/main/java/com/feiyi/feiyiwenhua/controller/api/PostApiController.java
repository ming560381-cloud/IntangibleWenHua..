package com.feiyi.feiyiwenhua.controller.api;

import com.feiyi.feiyiwenhua.dto.PostLikeResponse;
import com.feiyi.feiyiwenhua.entity.Post;
import com.feiyi.feiyiwenhua.service.MinioImageService;
import com.feiyi.feiyiwenhua.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/community/posts")
public class PostApiController {

    @Autowired
    private PostService postService;

    @Autowired
    private MinioImageService minioImageService;

    @GetMapping
    public ResponseEntity<List<Post>> list(HttpServletRequest request) {
        return ResponseEntity.ok(postService.findPublishedPosts(currentUserId(request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> detail(@PathVariable Long id, HttpServletRequest request) {
        Post existingPost = postService.findById(id);
        if (existingPost == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        postService.incrementViewCount(id);
        Post post = postService.findById(id, currentUserId(request));
        if (post == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(post);
    }

    @PostMapping
    public ResponseEntity<Post> create(@RequestBody Post post) {
        // 验证必要字段
        if (post.getTitle() == null || post.getContent() == null || post.getAuthorId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(postService.save(post));
    }

    @PostMapping(value = "/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        Long userId = currentUserId(request);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("请先登录后再上传图片。");
        }

        try {
            String imageUrl = minioImageService.uploadCommunityImage(userId, file);
            Map<String, String> result = new HashMap<>();
            result.put("url", imageUrl);
            return ResponseEntity.ok(result);
        } catch (RuntimeException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Post> update(@PathVariable Long id, @RequestBody Post post) {
        Post existingPost = postService.findById(id);
        if (existingPost == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        post.setId(id);
        return ResponseEntity.ok(postService.update(post));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Post existingPost = postService.findById(id);
        if (existingPost == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        postService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<PostLikeResponse> like(@PathVariable Long id, HttpServletRequest request) {
        Post existingPost = postService.findById(id);
        if (existingPost == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Long userId = currentUserId(request);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(postService.toggleLike(id, userId));
    }

    @PostMapping("/{id}/view")
    public ResponseEntity<Void> view(@PathVariable Long id) {
        Post existingPost = postService.findById(id);
        if (existingPost == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        postService.incrementViewCount(id);
        return ResponseEntity.ok().build();
    }

    private Long currentUserId(HttpServletRequest request) {
        // 先从session中获取用户ID
        HttpSession session = request.getSession(false);
        if (session != null) {
            Object userId = session.getAttribute("userId");
            if (userId instanceof Long) {
                return (Long) userId;
            }
            if (userId instanceof Integer) {
                return ((Integer) userId).longValue();
            }
        }

        // 如果session中没有，从请求头中获取
        String userIdStr = request.getHeader("X-User-Id");
        if (userIdStr != null && !userIdStr.isEmpty()) {
            try {
                return Long.parseLong(userIdStr);
            } catch (NumberFormatException e) {
                // 忽略格式错误
            }
        }

        return null;
    }
}
