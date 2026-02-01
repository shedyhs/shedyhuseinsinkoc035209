package com.shedyhuseinsinkoc035209.config;

import com.shedyhuseinsinkoc035209.filter.JwtAuthenticationFilter;
import com.shedyhuseinsinkoc035209.filter.RateLimitFilter;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class SecurityConfigTest {

    @Test
    void passwordEncoder_shouldReturnBCryptEncoder() {
        JwtAuthenticationFilter jwtFilter = mock(JwtAuthenticationFilter.class);
        RateLimitFilter rateLimitFilter = mock(RateLimitFilter.class);
        SecurityConfig config = new SecurityConfig(jwtFilter, rateLimitFilter);

        PasswordEncoder encoder = config.passwordEncoder();

        assertThat(encoder).isInstanceOf(BCryptPasswordEncoder.class);
    }

    @Test
    void corsConfigurationSource_shouldReturnConfiguredSource() {
        JwtAuthenticationFilter jwtFilter = mock(JwtAuthenticationFilter.class);
        RateLimitFilter rateLimitFilter = mock(RateLimitFilter.class);
        SecurityConfig config = new SecurityConfig(jwtFilter, rateLimitFilter);
        ReflectionTestUtils.setField(config, "allowedOrigins", "http://localhost:8080");

        CorsConfigurationSource source = config.corsConfigurationSource();

        assertThat(source).isInstanceOf(UrlBasedCorsConfigurationSource.class);
    }
}
