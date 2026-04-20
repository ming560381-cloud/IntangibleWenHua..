package com.feiyi.feiyiwenhua.controller.api;

import com.feiyi.feiyiwenhua.entity.User;
import com.feiyi.feiyiwenhua.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
public class UserApiController {

    @Autowired
    private UserService userService;

    // 用户注册
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();
        try {
            User registeredUser = userService.register(user);
            response.put("success", true);
            response.put("message", "注册成功");
            response.put("data", registeredUser);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // 密码登录
    @PostMapping("/login/password")
    public ResponseEntity<Map<String, Object>> loginWithPassword(
            @RequestBody Map<String, String> credentials,
            HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            String phone = credentials.get("phone");
            String password = credentials.get("password");

            User user = userService.loginWithPassword(phone, password);

            // 创建session
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            session.setAttribute("userId", user.getId());
            session.setAttribute("userRole", user.getRole());

            response.put("success", true);
            response.put("message", "登录成功");
            response.put("data", user);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    // 验证码登录
    @PostMapping("/login/code")
    public ResponseEntity<Map<String, Object>> loginWithVerificationCode(
            @RequestBody Map<String, String> credentials,
            HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            String phone = credentials.get("phone");
            String code = credentials.get("code");

            User user = userService.loginWithVerificationCode(phone, code);

            // 创建session
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            session.setAttribute("userId", user.getId());
            session.setAttribute("userRole", user.getRole());

            response.put("success", true);
            response.put("message", "登录成功");
            response.put("data", user);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    // 发送验证码
    @PostMapping("/send-code")
    public ResponseEntity<Map<String, Object>> sendVerificationCode(@RequestBody Map<String, String> requestBody) {
        Map<String, Object> response = new HashMap<>();
        try {
            String phone = requestBody.get("phone");
            boolean success = userService.sendVerificationCode(phone);

            if (success) {
                response.put("success", true);
                response.put("message", "验证码发送成功");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "验证码发送失败");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // 获取当前用户信息
    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getProfile(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        Long userId = getUserIdFromRequest(request);

        if (userId == null) {
            response.put("success", false);
            response.put("message", "用户未登录");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        User user = userService.findById(userId);

        if (user == null) {
            response.put("success", false);
            response.put("message", "用户不存在");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        // 不返回密码等敏感信息
        user.setPassword(null);
        user.setVerificationCode(null);

        response.put("success", true);
        response.put("data", user);
        return ResponseEntity.ok(response);
    }

    // 从请求中获取用户ID
    private Long getUserIdFromRequest(HttpServletRequest request) {
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

    // 更新用户信息
    @PutMapping("/profile")
    public ResponseEntity<Map<String, Object>> updateProfile(
            @RequestBody User user,
            HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        Long userId = getUserIdFromRequest(request);

        if (userId == null) {
            response.put("success", false);
            response.put("message", "用户未登录");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        User existingUser = userService.findById(userId);

        if (existingUser == null) {
            response.put("success", false);
            response.put("message", "用户不存在");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        // 只允许更新部分字段
        if (user.getUsername() != null) {
            existingUser.setUsername(user.getUsername());
        }
        if (user.getAvatar() != null) {
            existingUser.setAvatar(user.getAvatar());
        }
        // 不允许直接修改手机号
        // 密码修改需要单独接口

        existingUser.setUpdateTime(new java.util.Date());
        User updatedUser = userService.save(existingUser);

        // 不返回密码等敏感信息
        updatedUser.setPassword(null);
        updatedUser.setVerificationCode(null);

        response.put("success", true);
        response.put("message", "更新成功");
        response.put("data", updatedUser);
        return ResponseEntity.ok(response);
    }

    // 退出登录
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }

        response.put("success", true);
        response.put("message", "退出成功");
        return ResponseEntity.ok(response);
    }

    // 检查登录状态
    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> checkAuth(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            response.put("authenticated", false);
            response.put("message", "用户未登录");
            return ResponseEntity.ok(response);
        }

        Long userId = (Long) session.getAttribute("userId");
        String userRole = (String) session.getAttribute("userRole");
        User user = userService.findById(userId);

        if (user == null) {
            response.put("authenticated", false);
            response.put("message", "用户不存在");
            return ResponseEntity.ok(response);
        }

        // 不返回密码等敏感信息
        user.setPassword(null);
        user.setVerificationCode(null);

        response.put("authenticated", true);
        response.put("user", user);
        response.put("role", userRole);
        return ResponseEntity.ok(response);
    }

    // 上传头像
    @PostMapping("/avatar")
    public ResponseEntity<Map<String, Object>> uploadAvatar(
            @RequestParam("avatar") MultipartFile file,
            HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        Long userId = getUserIdFromRequest(request);

        if (userId == null) {
            response.put("success", false);
            response.put("message", "用户未登录");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        if (file.isEmpty()) {
            response.put("success", false);
            response.put("message", "请选择要上传的文件");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            // 确保上传目录存在
            String uploadDir = "src/main/resources/static/avatars/";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : ".jpg";
            String filename = UUID.randomUUID().toString() + fileExtension;
            Path filePath = uploadPath.resolve(filename);

            // 保存文件
            Files.copy(file.getInputStream(), filePath);

            // 更新用户头像路径
            User user = userService.findById(userId);
            if (user == null) {
                response.put("success", false);
                response.put("message", "用户不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            String avatarUrl = "/avatars/" + filename;
            user.setAvatar(avatarUrl);
            user.setUpdateTime(new java.util.Date());
            User updatedUser = userService.save(user);

            // 不返回密码等敏感信息
            updatedUser.setPassword(null);
            updatedUser.setVerificationCode(null);

            response.put("success", true);
            response.put("message", "头像上传成功");
            response.put("data", updatedUser);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "头像上传失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}