package com.dongh.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dongh.admin.dto.RegisterRequest;
import com.dongh.admin.entity.User;
import com.dongh.admin.mapper.UserMapper;
import com.dongh.admin.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public User getByUsername(String username) {
        return lambdaQuery().eq(User::getUsername, username).one();
    }

    @Override
    public boolean existsByUsername(String username) {
        return lambdaQuery().eq(User::getUsername, username).exists();
    }

    @Override
    public User register(RegisterRequest request, PasswordEncoder passwordEncoder) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname());
        user.setEmail(request.getEmail());
        user.setStatus(1);
        save(user);
        return user;
    }
}
