package com.dongh.admin.dto;

import lombok.Builder;
import lombok.Data;

/**
 * Authentication response: returns tokens and user info after login/register/refresh.
 */
@Data
@Builder
public class AuthResponse {

    private String accessToken;

    private String refreshToken;

    /** Always "Bearer", used with the Authorization: Bearer <token> header. */
    private String tokenType;

    /** Access token validity period (seconds). */
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
