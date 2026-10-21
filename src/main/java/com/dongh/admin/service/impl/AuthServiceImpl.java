package com.dongh.admin.service.impl;

import com.dongh.admin.common.ResultCode;
import com.dongh.admin.common.exception.BusinessException;
import com.dongh.admin.dto.AuthResponse;
import com.dongh.admin.dto.LoginRequest;
import com.dongh.admin.dto.RegisterRequest;
import com.dongh.admin.entity.User;
import com.dongh.admin.mapper.PermissionMapper;
import com.dongh.admin.security.JwtUtil;
import com.dongh.admin.service.AuthService;
import com.dongh.admin.service.RedisService;
import com.dongh.admin.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 认证服务实现
 */
@Service
public class AuthServiceImpl implements AuthService {

    /** 登录态缓存 key 前缀 */
    private static final String LOGIN_KEY_PREFIX = "login:user:";

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final PermissionMapper permissionMapper;
    private final RedisService redisService;

    public AuthServiceImpl(UserService userService,
                           PasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil,
                           PermissionMapper permissionMapper,
                           RedisService redisService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.permissionMapper = permissionMapper;
        this.redisService = redisService;
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
        // 优先读登录态缓存，miss 再查库
        AuthResponse.UserInfo cached = redisService.get(LOGIN_KEY_PREFIX + username, AuthResponse.UserInfo.class);
        if (cached != null) {
            return cached;
        }
        User user = userService.getByUsername(username);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        }
        return toUserInfo(user);
    }

    @Override
    public void logout(String username) {
        redisService.delete(LOGIN_KEY_PREFIX + username);
    }

    private AuthResponse buildAuthResponse(User user) {
        // 登录/刷新时实时查询用户权限编码，写入 access token
        List<String> permissions = permissionMapper.selectPermissionCodesByUserId(user.getId());
        AuthResponse.UserInfo userInfo = toUserInfo(user);
        // 登录态缓存：缓存用户信息，TTL 与 access token 一致
        redisService.set(LOGIN_KEY_PREFIX + user.getUsername(), userInfo, jwtUtil.getAccessExpiration());
        return AuthResponse.builder()
                .accessToken(jwtUtil.generateAccessToken(user.getUsername(), permissions))
                .refreshToken(jwtUtil.generateRefreshToken(user.getUsername()))
                .tokenType("Bearer")
                .expiresIn(jwtUtil.getAccessExpiration())
                .user(userInfo)
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
