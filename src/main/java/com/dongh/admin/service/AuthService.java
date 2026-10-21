package com.dongh.admin.service;

import com.dongh.admin.dto.AuthResponse;
import com.dongh.admin.dto.LoginRequest;
import com.dongh.admin.dto.RegisterRequest;

/**
 * Authentication service: register / login / refresh token / current user
 */
public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refresh(String refreshToken);

    AuthResponse.UserInfo getCurrentUser(String username);

    void logout(String username);
}
