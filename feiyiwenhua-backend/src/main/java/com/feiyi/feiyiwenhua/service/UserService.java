package com.feiyi.feiyiwenhua.service;

import com.feiyi.feiyiwenhua.entity.User;

import java.util.List;

public interface UserService {
    List<User> findAll();

    User findById(Long id);

    User findByPhone(String phone);

    User findByUsername(String username);

    User save(User user);

    void deleteById(Long id);

    User register(User user);

    User loginWithPassword(String phone, String password);

    User loginWithVerificationCode(String phone, String code);

    boolean sendVerificationCode(String phone);

    boolean verifyCode(String phone, String code);

    boolean isAdmin(User user);

    boolean isUser(User user);

    boolean enableUser(Long id);

    boolean disableUser(Long id);
}
