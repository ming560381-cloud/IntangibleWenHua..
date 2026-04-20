package com.feiyi.feiyiwenhua.controller.api;

import com.feiyi.feiyiwenhua.entity.Activity;
import com.feiyi.feiyiwenhua.entity.ActivityRegistration;
import com.feiyi.feiyiwenhua.entity.User;
import com.feiyi.feiyiwenhua.service.ActivityService;
import com.feiyi.feiyiwenhua.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/api/activity")
public class ActivityApiController {

    @Autowired
    private ActivityService activityService;
    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<Activity>> list() {
        return ResponseEntity.ok(activityService.findPublished());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Activity> detail(@PathVariable Long id) {
        Activity activity = activityService.findById(id);
        if (activity == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(activity);
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody Activity activity, HttpServletRequest request) {
        ResponseEntity<String> auth = requireAdmin(request);
        if (auth != null) {
            return auth;
        }
        return ResponseEntity.ok(activityService.save(activity));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Activity activity, HttpServletRequest request) {
        ResponseEntity<String> auth = requireAdmin(request);
        if (auth != null) {
            return auth;
        }
        Activity existing = activityService.findById(id);
        if (existing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        activity.setId(id);
        return ResponseEntity.ok(activityService.save(activity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, HttpServletRequest request) {
        ResponseEntity<String> auth = requireAdmin(request);
        if (auth != null) {
            return auth;
        }
        activityService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/register")
    public ResponseEntity<?> register(@PathVariable Long id, @RequestBody ActivityRegistration registration) {
        if (registration.getParticipantName() == null || registration.getParticipantName().trim().isEmpty()
                || registration.getPhone() == null || registration.getPhone().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Please provide participant name and phone.");
        }
        try {
            return ResponseEntity.ok(activityService.register(id, registration));
        } catch (RuntimeException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
        }
    }

    @GetMapping("/{id}/registrations")
    public ResponseEntity<?> registrations(@PathVariable Long id, HttpServletRequest request) {
        ResponseEntity<String> auth = requireAdmin(request);
        if (auth != null) {
            return auth;
        }
        return ResponseEntity.ok(activityService.findRegistrationsByActivity(id));
    }

    @GetMapping("/user/registrations")
    public ResponseEntity<?> getUserRegistrations(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("用户未登录");
        }
        Long userId = (Long) session.getAttribute("userId");
        return ResponseEntity.ok(activityService.findRegistrationsByUser(userId));
    }

    @DeleteMapping("/registration/{registrationId}")
    public ResponseEntity<?> cancelRegistration(@PathVariable Long registrationId, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("用户未登录");
        }
        Long userId = (Long) session.getAttribute("userId");
        try {
            activityService.cancelRegistration(registrationId, userId);
            return ResponseEntity.ok("取消报名成功");
        } catch (RuntimeException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
        }
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