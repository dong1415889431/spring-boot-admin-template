package com.dongh.admin.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JWT 工具类单元测试（纯逻辑，无需 Spring 上下文）
 */
class JwtUtilTest {

    private static final String SECRET = "unit-test-secret-key-at-least-32-bytes-long!!";

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", SECRET);
        ReflectionTestUtils.setField(jwtUtil, "accessExpiration", 3600L);
        ReflectionTestUtils.setField(jwtUtil, "refreshExpiration", 604800L);
        jwtUtil.init();
    }

    @Test
    void generateAndParseAccessToken() {
        List<String> permissions = List.of("user:list", "article:list");
        String token = jwtUtil.generateAccessToken("admin", permissions);

        assertEquals("admin", jwtUtil.getUsername(token));
        assertTrue(jwtUtil.isAccessToken(token));
        assertFalse(jwtUtil.isRefreshToken(token));
        assertEquals(permissions, jwtUtil.getPermissions(token));
        assertTrue(jwtUtil.isValid(token));
    }

    @Test
    void refreshTokenCarriesNoPermissions() {
        String token = jwtUtil.generateRefreshToken("admin");

        assertTrue(jwtUtil.isRefreshToken(token));
        assertFalse(jwtUtil.isAccessToken(token));
        assertTrue(jwtUtil.getPermissions(token).isEmpty());
    }

    @Test
    void invalidTokenShouldFailValidation() {
        assertFalse(jwtUtil.isValid("not-a-valid-token"));
    }
}
