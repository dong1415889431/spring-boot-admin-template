package com.dongh.admin.controller;

import com.dongh.admin.common.Result;
import com.dongh.admin.dto.AuthResponse;
import com.dongh.admin.dto.LoginRequest;
import com.dongh.admin.dto.RefreshTokenRequest;
import com.dongh.admin.dto.RegisterRequest;
import com.dongh.admin.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证接口：注册 / 登录 / 刷新 token / 当前用户
 */
@Tag(name = "认证管理", description = "用户注册、登录、JWT 刷新与当前用户信息")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return Result.success(authService.register(request));
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.success(authService.login(request));
    }

    @Operation(summary = "刷新 token")
    @PostMapping("/refresh")
    public Result<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return Result.success(authService.refresh(request.getRefreshToken()));
    }

    @Operation(summary = "当前登录用户信息（需携带 token）")
    @GetMapping("/me")
    public Result<AuthResponse.UserInfo> me() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return Result.success(authService.getCurrentUser(authentication.getName()));
    }

    @Operation(summary = "退出登录（清除登录态缓存，需携带 token）")
    @PostMapping("/logout")
    public Result<Void> logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        authService.logout(authentication.getName());
        return Result.success();
    }
}
