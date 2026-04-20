package com.feiyi.feiyiwenhua.controller.api;

import com.feiyi.feiyiwenhua.entity.Master;
import com.feiyi.feiyiwenhua.entity.User;
import com.feiyi.feiyiwenhua.service.MasterService;
import com.feiyi.feiyiwenhua.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/api/master")
public class MasterApiController {

    @Autowired
    private MasterService masterService;
    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<Master>> list() {
        return ResponseEntity.ok(masterService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Master> detail(@PathVariable Long id) {
        Master master = masterService.findById(id);
        if (master == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(master);
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody Master master, HttpServletRequest request) {
        ResponseEntity<String> auth = requireAdmin(request);
        if (auth != null) {
            return auth;
        }
        try {
            return ResponseEntity.ok(masterService.save(master));
        } catch (DataIntegrityViolationException ex) {
            return ResponseEntity.badRequest().body("保存失败：文本内容过长，请执行数据库字段升级脚本后重试");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Master master, HttpServletRequest request) {
        ResponseEntity<String> auth = requireAdmin(request);
        if (auth != null) {
            return auth;
        }
        Master existingMaster = masterService.findById(id);
        if (existingMaster == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        master.setId(id);
        try {
            return ResponseEntity.ok(masterService.save(master));
        } catch (DataIntegrityViolationException ex) {
            return ResponseEntity.badRequest().body("更新失败：文本内容过长，请执行数据库字段升级脚本后重试");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, HttpServletRequest request) {
        ResponseEntity<String> auth = requireAdmin(request);
        if (auth != null) {
            return auth;
        }
        masterService.deleteById(id);
        return ResponseEntity.ok().build();
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