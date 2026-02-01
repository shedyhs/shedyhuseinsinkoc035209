package com.shedyhuseinsinkoc035209.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", "mySuperSecretKeyForJwtTokenGenerationThatIsLongEnough123456");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 3600000L);
        ReflectionTestUtils.setField(jwtUtil, "refreshExpiration", 86400000L);
    }

    @Test
    void generateToken_shouldReturnValidToken() {
        String token = jwtUtil.generateToken("admin");

        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
    }

    @Test
    void generateRefreshToken_shouldReturnValidToken() {
        String token = jwtUtil.generateRefreshToken("admin");

        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
    }

    @Test
    void extractUsername_shouldReturnCorrectUsername() {
        String token = jwtUtil.generateToken("admin");

        String username = jwtUtil.extractUsername(token);

        assertThat(username).isEqualTo("admin");
    }

    @Test
    void validateToken_shouldReturnTrueForValidToken() {
        String token = jwtUtil.generateToken("admin");

        boolean isValid = jwtUtil.validateToken(token);

        assertThat(isValid).isTrue();
    }

    @Test
    void validateToken_shouldReturnFalseForInvalidToken() {
        boolean isValid = jwtUtil.validateToken("invalid.token.here");

        assertThat(isValid).isFalse();
    }

    @Test
    void validateToken_shouldReturnFalseForWrongSignature() {
        JwtUtil otherJwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(otherJwtUtil, "secret", "anotherSecretKeyThatIsDifferentFromTheOriginalOne12345678");
        ReflectionTestUtils.setField(otherJwtUtil, "expiration", 3600000L);
        ReflectionTestUtils.setField(otherJwtUtil, "refreshExpiration", 86400000L);

        String token = otherJwtUtil.generateToken("admin");

        boolean isValid = jwtUtil.validateToken(token);

        assertThat(isValid).isFalse();
    }

    @Test
    void extractExpiration_shouldReturnFutureDate() {
        String token = jwtUtil.generateToken("admin");

        Date expiration = jwtUtil.extractExpiration(token);

        assertThat(expiration).isAfter(new Date());
    }

    @Test
    void isRefreshToken_shouldReturnTrueForRefreshToken() {
        String token = jwtUtil.generateRefreshToken("admin");

        assertThat(jwtUtil.isRefreshToken(token)).isTrue();
    }

    @Test
    void isRefreshToken_shouldReturnFalseForAccessToken() {
        String token = jwtUtil.generateToken("admin");

        assertThat(jwtUtil.isRefreshToken(token)).isFalse();
    }
}
