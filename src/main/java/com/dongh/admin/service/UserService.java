package com.dongh.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dongh.admin.dto.RegisterRequest;
import com.dongh.admin.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 用户服务
 */
public interface UserService extends IService<User> {

    User getByUsername(String username);

    boolean existsByUsername(String username);

    User register(RegisterRequest request, PasswordEncoder passwordEncoder);
}
