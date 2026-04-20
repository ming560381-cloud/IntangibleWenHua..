package com.feiyi.feiyiwenhua.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.feiyi.feiyiwenhua.entity.User;
import com.feiyi.feiyiwenhua.repository.UserRepository;
import com.feiyi.feiyiwenhua.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<User> findAll() {
        return userRepository.selectList(null);
    }

    @Override
    public User findById(Long id) {
        return userRepository.selectById(id);
    }

    @Override
    public User findByPhone(String phone) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone, phone);
        return userRepository.selectOne(queryWrapper);
    }

    @Override
    public User findByUsername(String username) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        return userRepository.selectOne(queryWrapper);
    }

    @Override
    public User save(User user) {
        Date now = new Date();
        if (user.getId() != null) {
            user.setUpdateTime(now);
            userRepository.updateById(user);
        } else {
            user.setCreateTime(now);
            user.setUpdateTime(now);
            userRepository.insert(user);
        }
        return user;
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public User register(User user) {
        if (findByPhone(user.getPhone()) != null) {
            throw new RuntimeException("手机号已注册");
        }

        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            user.setUsername(user.getPhone());
        }

        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(encryptPassword(user.getPassword()));
        } else {
            user.setPassword(encryptPassword(user.getPhone()));
        }

        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("user");
        }
        if (user.getStatus() == null) {
            user.setStatus(1);
        }

        return save(user);
    }

    @Override
    public User loginWithPassword(String phone, String password) {
        User user = findByPhone(phone);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用");
        }

        String encryptedPassword = encryptPassword(password);
        if (!encryptedPassword.equals(user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        return user;
    }

    @Override
    public User loginWithVerificationCode(String phone, String code) {
        User user = findByPhone(phone);
        if (user == null) {
            user = new User();
            user.setPhone(phone);
            user.setUsername(phone);
            user.setPassword(phone);
            user.setRole("user");
            user.setStatus(1);
            user = register(user);
        } else if (user.getStatus() != null && user.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用");
        }

        if (!"123456".equals(code)) {
            throw new RuntimeException("验证码错误");
        }

        return user;
    }

    @Override
    public boolean sendVerificationCode(String phone) {
        String code = generateVerificationCode();

        User user = findByPhone(phone);
        if (user == null) {
            user = new User();
            user.setPhone(phone);
            user.setUsername(phone);
            user.setPassword(encryptPassword(phone));
            user.setRole("user");
            user.setStatus(1);
        }
        user.setVerificationCode(code);
        user.setCodeExpireTime(new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5)));
        save(user);

        System.out.println("发送验证码到 " + phone + ": " + code);
        return true;
    }

    @Override
    public boolean verifyCode(String phone, String code) {
        User user = findByPhone(phone);
        if (user == null) {
            return false;
        }
        if (code == null || !code.equals(user.getVerificationCode())) {
            return false;
        }
        return user.getCodeExpireTime() != null && !user.getCodeExpireTime().before(new Date());
    }

    @Override
    public boolean isAdmin(User user) {
        return user != null && "admin".equals(user.getRole());
    }

    @Override
    public boolean isUser(User user) {
        return user != null && "user".equals(user.getRole());
    }

    @Override
    public boolean enableUser(Long id) {
        User user = findById(id);
        if (user == null) {
            return false;
        }
        user.setStatus(1);
        user.setUpdateTime(new Date());
        userRepository.updateById(user);
        return true;
    }

    @Override
    public boolean disableUser(Long id) {
        User user = findById(id);
        if (user == null) {
            return false;
        }
        user.setStatus(0);
        user.setUpdateTime(new Date());
        userRepository.updateById(user);
        return true;
    }

    private String encryptPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("密码加密失败", e);
        }
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
}
