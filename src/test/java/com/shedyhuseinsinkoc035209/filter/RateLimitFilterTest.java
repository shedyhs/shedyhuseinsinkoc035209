package com.shedyhuseinsinkoc035209.filter;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RateLimitFilterTest {

    @Mock
    private FilterChain filterChain;

    private RateLimitFilter rateLimitFilter;

    @BeforeEach
    void setUp() {
        rateLimitFilter = new RateLimitFilter();
        ReflectionTestUtils.setField(rateLimitFilter, "capacity", 10);
        ReflectionTestUtils.setField(rateLimitFilter, "refillPeriodMinutes", 1L);
        ReflectionTestUtils.setField(rateLimitFilter, "expirationMinutes", 2L);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldNotFilter_swaggerPaths() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/swagger-ui/index.html");

        assertThat(rateLimitFilter.shouldNotFilter(request)).isTrue();
    }

    @Test
    void shouldNotFilter_apiDocsPaths() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/v3/api-docs");

        assertThat(rateLimitFilter.shouldNotFilter(request)).isTrue();
    }

    @Test
    void shouldNotFilter_actuatorPaths() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/actuator/health");

        assertThat(rateLimitFilter.shouldNotFilter(request)).isTrue();
    }

    @Test
    void shouldFilter_apiPaths() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/api/v1/artists");

        assertThat(rateLimitFilter.shouldNotFilter(request)).isFalse();
    }

    @Test
    void doFilterInternal_shouldAllowRequestWithinLimit() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("192.168.1.1");
        MockHttpServletResponse response = new MockHttpServletResponse();

        rateLimitFilter.doFilterInternal(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(200);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_shouldUseUsernameAsKeyWhenAuthenticated() throws Exception {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken("admin", null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        rateLimitFilter.doFilterInternal(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(200);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_shouldReturn429WhenRateLimitExceeded() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("10.0.0.1");

        // Exhaust the 10 requests/minute limit
        for (int i = 0; i < 10; i++) {
            MockHttpServletResponse response = new MockHttpServletResponse();
            rateLimitFilter.doFilterInternal(request, response, filterChain);
        }

        // 11th request should be rate limited
        MockHttpServletResponse response = new MockHttpServletResponse();
        rateLimitFilter.doFilterInternal(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(429);
        assertThat(response.getContentType()).isEqualTo("application/json");
        assertThat(response.getContentAsString()).contains("Rate limit exceeded");
        verify(filterChain, times(10)).doFilter(any(), any());
    }
}
