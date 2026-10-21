package com.dongh.admin.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * BCrypt password encoder unit test
 */
class PasswordEncoderTest {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Test
    void encodeAndMatch() {
        String raw = "admin123";
        String encoded = encoder.encode(raw);

        assertNotEquals(raw, encoded);
        assertTrue(encoder.matches(raw, encoded));
    }

    @Test
    void wrongPasswordShouldNotMatch() {
        String encoded = encoder.encode("admin123");

        assertFalse(encoder.matches("wrong-password", encoded));
    }

    @Test
    void saltShouldBeRandom() {
        String first = encoder.encode("admin123");
        String second = encoder.encode("admin123");

        assertNotEquals(first, second);
    }
}
