package com.dongh.admin.service;

import com.dongh.admin.dto.AuthResponse;
import com.dongh.admin.dto.LoginRequest;
import com.dongh.admin.dto.RegisterRequest;

/**
 * 认证服务：注册 / 登录 / 刷新 token / 当前用户
 */
public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refresh(String refreshToken);

    AuthResponse.UserInfo getCurrentUser(String username);

    void logout(String username);
}
