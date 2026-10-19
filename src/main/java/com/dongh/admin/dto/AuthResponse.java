package com.dongh.admin.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 认证响应：登录/注册/刷新后返回 token 与用户信息
 */
@Data
@Builder
public class AuthResponse {

    private String accessToken;

    private String refreshToken;

    /** 固定为 Bearer，配合请求头 Authorization: Bearer <token> */
    private String tokenType;

    /** access token 有效期（秒） */
    private long expiresIn;

    private UserInfo user;

    @Data
    @Builder
    public static class UserInfo {
        private Long id;
        private String username;
        private String nickname;
        private String email;
    }
}
