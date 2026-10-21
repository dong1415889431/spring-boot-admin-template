package com.dongh.admin.service;

import com.dongh.admin.dto.RegisterRequest;
import com.dongh.admin.entity.User;
import com.dongh.admin.mapper.UserMapper;
import com.dongh.admin.mapper.UserRoleMapper;
import com.dongh.admin.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

/**
 * User service unit test (mocks dependencies with Mockito, no database required)
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserRoleMapper userRoleMapper;

    private UserServiceImpl userService;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRoleMapper);
        // Manually inject the baseMapper field inherited from ServiceImpl (MyBatis-Plus depends on it internally)
        ReflectionTestUtils.setField(userService, "baseMapper", userMapper);
    }

    @Test
    void registerShouldEncodePasswordAndSave() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setPassword("123456");
        request.setNickname("Test User");
        request.setEmail("test@example.com");

        User user = userService.register(request, passwordEncoder);

        assertNotNull(user);
        assertEquals("testuser", user.getUsername());
        assertTrue(passwordEncoder.matches("123456", user.getPassword()));
        assertEquals(1, user.getStatus());
        verify(userMapper).insert(any(User.class));
    }
}
