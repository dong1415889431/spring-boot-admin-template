package com.dongh.admin.service.impl;

import com.dongh.admin.common.ResultCode;
import com.dongh.admin.common.exception.BusinessException;
import com.dongh.admin.dto.AuthResponse;
import com.dongh.admin.dto.LoginRequest;
import com.dongh.admin.dto.RegisterRequest;
import com.dongh.admin.entity.User;
import com.dongh.admin.security.JwtUtil;
import com.dongh.admin.service.AuthService;
import com.dongh.admin.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 认证服务实现
 */
@Service
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(UserService userService, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userService.existsByUsername(request.getUsername())) {
            throw new BusinessException(ResultCode.CONFLICT, "用户名已存在");
        }
        User user = userService.register(request, passwordEncoder);
        return buildAuthResponse(user);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userService.getByUsername(request.getUsername());
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "用户名或密码错误");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException(ResultCode.FORBIDDEN, "账号已被禁用");
        }
        return buildAuthResponse(user);
    }

    @Override
    public AuthResponse refresh(String refreshToken) {
        if (!jwtUtil.isValid(refreshToken) || !jwtUtil.isRefreshToken(refreshToken)) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "refreshToken 无效或已过期");
        }
        User user = userService.getByUsername(jwtUtil.getUsername(refreshToken));
        if (user == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "用户不存在");
        }
        return buildAuthResponse(user);
    }

    @Override
    public AuthResponse.UserInfo getCurrentUser(String username) {
        User user = userService.getByUsername(username);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        }
        return toUserInfo(user);
    }

    private AuthResponse buildAuthResponse(User user) {
        return AuthResponse.builder()
                .accessToken(jwtUtil.generateAccessToken(user.getUsername()))
                .refreshToken(jwtUtil.generateRefreshToken(user.getUsername()))
                .tokenType("Bearer")
                .expiresIn(jwtUtil.getAccessExpiration())
                .user(toUserInfo(user))
                .build();
    }

    private AuthResponse.UserInfo toUserInfo(User user) {
        return AuthResponse.UserInfo.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .build();
    }
}
