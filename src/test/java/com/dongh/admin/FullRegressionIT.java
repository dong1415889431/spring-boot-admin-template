package com.dongh.admin;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Full end-to-end regression test.
 *
 * <p>Exercises the complete flow — health check, register, login, unauthorized
 * access, article CRUD, permission denial, and logout — against a real MySQL and
 * Redis spun up via Testcontainers.</p>
 *
 * <p>Requires Docker. Run with {@code mvn verify} (integration tests are named
 * {@code *IT} so {@code mvn test} skips them).</p>
 */
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// TestRestTemplate's Class-based methods are soft-deprecated in Spring 6.1
// (in favor of RestClient); they remain the standard tool for integration tests.
@SuppressWarnings({"deprecation", "unchecked"})
class FullRegressionIT {

    @Container
    @ServiceConnection
    static final MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("admin_template")
            .withInitScript("db/schema.sql");

    @Container
    @ServiceConnection
    static final GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
            .withExposedPorts(6379);

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void fullRegressionFlow() {
        // 1. Health check
        ResponseEntity<Map> health = restTemplate.getForEntity("/api/health", Map.class);
        assertEquals(200, health.getStatusCodeValue());
        assertEquals("UP", health.getBody().get("status"));

        // 2. Register a new (unprivileged) user
        String username = "it_" + System.currentTimeMillis();
        Map<String, Object> reg = new HashMap<>();
        reg.put("username", username);
        reg.put("password", "test123456");
        reg.put("nickname", "IT User");
        ResponseEntity<Map> regResp = restTemplate.postForEntity("/api/auth/register", reg, Map.class);
        assertEquals(0, ((Number) regResp.getBody().get("code")).intValue());
        String userToken = tokenOf(regResp);

        // 3. Login as admin
        Map<String, Object> login = new HashMap<>();
        login.put("username", "admin");
        login.put("password", "admin123");
        ResponseEntity<Map> loginResp = restTemplate.postForEntity("/api/auth/login", login, Map.class);
        assertEquals(0, ((Number) loginResp.getBody().get("code")).intValue());
        String adminToken = tokenOf(loginResp);
        assertNotNull(adminToken);

        // 4. No token -> 401
        ResponseEntity<Map> noAuth = restTemplate.getForEntity("/api/articles", Map.class);
        assertEquals(401, noAuth.getStatusCodeValue());

        HttpHeaders adminHeaders = bearer(adminToken);

        // 5. List articles
        ResponseEntity<Map> list = exchange(HttpMethod.GET, "/api/articles?page=1&size=10", null, adminHeaders);
        assertEquals(0, ((Number) list.getBody().get("code")).intValue());

        // 6. Create article
        Map<String, Object> article = new HashMap<>();
        article.put("title", "IT Regression Article");
        article.put("content", "created by FullRegressionIT");
        article.put("author", "admin");
        article.put("status", 1);
        ResponseEntity<Map> created = exchange(HttpMethod.POST, "/api/articles", article, adminHeaders);
        assertEquals(0, ((Number) created.getBody().get("code")).intValue());
        long articleId = ((Number) ((Map<?, ?>) created.getBody().get("data")).get("id")).longValue();

        // 7. Update article
        article.put("title", "IT Regression Article (updated)");
        ResponseEntity<Map> updated = exchange(HttpMethod.PUT, "/api/articles/" + articleId, article, adminHeaders);
        assertEquals(0, ((Number) updated.getBody().get("code")).intValue());

        // 8. Article detail
        ResponseEntity<Map> detail = exchange(HttpMethod.GET, "/api/articles/" + articleId, null, adminHeaders);
        assertEquals(0, ((Number) detail.getBody().get("code")).intValue());

        // 9. Delete article
        ResponseEntity<Map> deleted = exchange(HttpMethod.DELETE, "/api/articles/" + articleId, null, adminHeaders);
        assertEquals(0, ((Number) deleted.getBody().get("code")).intValue());

        // 10. Permission control -> 403 (unprivileged user has no article:* permission)
        ResponseEntity<Map> denied = exchange(HttpMethod.GET, "/api/articles?page=1&size=10", null, bearer(userToken));
        assertEquals(403, denied.getStatusCodeValue());

        // 11. Logout
        ResponseEntity<Map> logout = exchange(HttpMethod.POST, "/api/auth/logout", null, adminHeaders);
        assertEquals(0, ((Number) logout.getBody().get("code")).intValue());
    }

    private ResponseEntity<Map> exchange(HttpMethod method, String path, Map<?, ?> body, HttpHeaders headers) {
        return restTemplate.exchange(path, method, new HttpEntity<>(body, headers), Map.class);
    }

    private HttpHeaders bearer(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return headers;
    }

    @SuppressWarnings("unchecked")
    private String tokenOf(ResponseEntity<Map> resp) {
        return (String) ((Map<String, Object>) resp.getBody().get("data")).get("accessToken");
    }
}
