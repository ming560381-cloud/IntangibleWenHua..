package com.feiyi.feiyiwenhua.controller.api;

import com.feiyi.feiyiwenhua.dto.HeritageVideoUploadResponse;
import com.feiyi.feiyiwenhua.entity.Heritage;
import com.feiyi.feiyiwenhua.entity.User;
import com.feiyi.feiyiwenhua.service.HeritageService;
import com.feiyi.feiyiwenhua.service.MinioVideoService;
import com.feiyi.feiyiwenhua.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/api/heritage")
public class HeritageApiController {

    @Autowired
    private HeritageService heritageService;

    @Autowired
    private UserService userService;

    @Autowired
    private MinioVideoService minioVideoService;

    @GetMapping
    public ResponseEntity<List<Heritage>> list() {
        return ResponseEntity.ok(heritageService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Heritage> detail(@PathVariable Long id) {
        Heritage heritage = heritageService.findById(id);
        if (heritage == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(heritage);
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody Heritage heritage, HttpServletRequest request) {
        ResponseEntity<String> auth = requireAdmin(request);
        if (auth != null) {
            return auth;
        }
        return ResponseEntity.ok(heritageService.save(heritage));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Heritage heritage, HttpServletRequest request) {
        ResponseEntity<String> auth = requireAdmin(request);
        if (auth != null) {
            return auth;
        }
        Heritage existingHeritage = heritageService.findById(id);
        if (existingHeritage == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        heritage.setId(id);
        return ResponseEntity.ok(heritageService.save(heritage));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, HttpServletRequest request) {
        ResponseEntity<String> auth = requireAdmin(request);
        if (auth != null) {
            return auth;
        }
        heritageService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/category/{id}")
    public ResponseEntity<List<Heritage>> byCategory(@PathVariable Long id) {
        return ResponseEntity.ok(heritageService.findByCategoryId(id));
    }

    @GetMapping("/level/{level}")
    public ResponseEntity<List<Heritage>> byLevel(@PathVariable Integer level) {
        return ResponseEntity.ok(heritageService.findByLevel(level));
    }

    @PostMapping(value = "/{id}/video", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadVideo(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {
        ResponseEntity<String> auth = requireAdmin(request);
        if (auth != null) {
            if (auth.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("请先登录管理员账号后再上传视频。");
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("只有管理员可以上传或替换项目视频。");
        }

        Heritage heritage = heritageService.findById(id);
        if (heritage == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("未找到对应的非遗项目。");
        }

        String videoUrl = minioVideoService.uploadHeritageVideo(id, file);
        heritage.setVideoUrl(videoUrl);
        heritageService.save(heritage);

        HeritageVideoUploadResponse response = new HeritageVideoUploadResponse(
                heritage.getId(),
                heritage.getVideoUrl(),
                extractObjectName(videoUrl),
                "视频上传成功。"
        );
        return ResponseEntity.ok(response);
    }

    private String extractObjectName(String videoUrl) {
        if (videoUrl == null || videoUrl.isEmpty()) {
            return "";
        }
        int bucketIndex = videoUrl.indexOf("/heritage/");
        if (bucketIndex >= 0) {
            return videoUrl.substring(bucketIndex + 1);
        }
        int protocolIndex = videoUrl.indexOf("://");
        if (protocolIndex >= 0) {
            int pathIndex = videoUrl.indexOf('/', protocolIndex + 3);
            if (pathIndex >= 0 && pathIndex + 1 < videoUrl.length()) {
                return videoUrl.substring(pathIndex + 1);
            }
        }
        return videoUrl;
    }

    private ResponseEntity<String> requireAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("用户未登录");
        }
        Long userId = (Long) session.getAttribute("userId");
        User currentUser = userService.findById(userId);
        if (!userService.isAdmin(currentUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("仅管理员可执行该操作");
        }
        return null;
    }
}
