package com.dongh.admin.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

/**
 * JWT 工具类（jjwt 0.12.x）
 *
 * 生成 / 解析 access token 与 refresh token，用 claim 中的 type 区分。
 * access token 额外携带权限编码（permissions claim），供 RBAC 鉴权使用。
 */
@Component
public class JwtUtil {

    private static final String CLAIM_TYPE = "type";
    private static final String CLAIM_PERMISSIONS = "permissions";
    private static final String TYPE_ACCESS = "access";
    private static final String TYPE_REFRESH = "refresh";

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long accessExpiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    private SecretKey key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(String username, List<String> permissions) {
        return generateToken(username, TYPE_ACCESS, accessExpiration, permissions);
    }

    public String generateRefreshToken(String username) {
        return generateToken(username, TYPE_REFRESH, refreshExpiration, List.of());
    }

    public String getUsername(String token) {
        return parseClaims(token).getSubject();
    }

    @SuppressWarnings("unchecked")
    public List<String> getPermissions(String token) {
        List<?> permissions = parseClaims(token).get(CLAIM_PERMISSIONS, List.class);
        if (permissions == null) {
            return List.of();
        }
        return permissions.stream().map(String::valueOf).toList();
    }

    public boolean isAccessToken(String token) {
        return TYPE_ACCESS.equals(getType(token));
    }

    public boolean isRefreshToken(String token) {
        return TYPE_REFRESH.equals(getType(token));
    }

    public boolean isValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public long getAccessExpiration() {
        return accessExpiration;
    }

    private String getType(String token) {
        return parseClaims(token).get(CLAIM_TYPE, String.class);
    }

    private String generateToken(String username, String type, long expirationSeconds, List<String> permissions) {
        JwtBuilder builder = Jwts.builder()
                .subject(username)
                .claim(CLAIM_TYPE, type)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationSeconds * 1000));
        if (permissions != null && !permissions.isEmpty()) {
            builder.claim(CLAIM_PERMISSIONS, permissions);
        }
        return builder.signWith(key).compact();
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
