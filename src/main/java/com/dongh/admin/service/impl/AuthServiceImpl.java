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
 * Authentication service implementation
 */
@Service
public class AuthServiceImpl implements AuthService {

    /** Login-state cache key prefix */
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
            throw new BusinessException(ResultCode.CONFLICT, "Username already exists");
        }
        User user = userService.register(request, passwordEncoder);
        return buildAuthResponse(user);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userService.getByUsername(request.getUsername());
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "Invalid username or password");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException(ResultCode.FORBIDDEN, "Account is disabled");
        }
        return buildAuthResponse(user);
    }

    @Override
    public AuthResponse refresh(String refreshToken) {
        if (!jwtUtil.isValid(refreshToken) || !jwtUtil.isRefreshToken(refreshToken)) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "refreshToken is invalid or expired");
        }
        User user = userService.getByUsername(jwtUtil.getUsername(refreshToken));
        if (user == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "User not found");
        }
        return buildAuthResponse(user);
    }

    @Override
    public AuthResponse.UserInfo getCurrentUser(String username) {
        // Read the login-state cache first; fall back to the database on a miss
        AuthResponse.UserInfo cached = redisService.get(LOGIN_KEY_PREFIX + username, AuthResponse.UserInfo.class);
        if (cached != null) {
            return cached;
        }
        User user = userService.getByUsername(username);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "User not found");
        }
        return toUserInfo(user);
    }

    @Override
    public void logout(String username) {
        redisService.delete(LOGIN_KEY_PREFIX + username);
    }

    private AuthResponse buildAuthResponse(User user) {
        // Query permission codes in real time on login/refresh and embed them in the access token
        List<String> permissions = permissionMapper.selectPermissionCodesByUserId(user.getId());
        AuthResponse.UserInfo userInfo = toUserInfo(user);
        // Login-state cache: cache the user info with the same TTL as the access token
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
