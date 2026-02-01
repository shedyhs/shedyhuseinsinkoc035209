package com.shedyhuseinsinkoc035209.filter;

import io.github.bucket4j.Bucket;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private static final Logger LOG = LoggerFactory.getLogger(RateLimitFilter.class);

    @Value("${rate-limit.capacity:10}")
    private int capacity;

    @Value("${rate-limit.refill-period-minutes:1}")
    private long refillPeriodMinutes;

    @Value("${rate-limit.expiration-minutes:2}")
    private long expirationMinutes;

    private final ConcurrentHashMap<String, TimestampedBucket> buckets = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @PostConstruct
    public void init() {
        scheduler.scheduleAtFixedRate(this::evictExpiredEntries, expirationMinutes, expirationMinutes, TimeUnit.MINUTES);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/actuator");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String key = resolveKey(request);
        TimestampedBucket timestamped = buckets.computeIfAbsent(key, k -> new TimestampedBucket(createBucket()));
        timestamped.touch();

        if (timestamped.getBucket().tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            LOG.warn("Rate limit exceeded for key '{}'", key);
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            String body = String.format(
                    "{\"timestamp\":\"%s\",\"status\":429,\"error\":\"Too Many Requests\","
                            + "\"message\":\"Rate limit exceeded. Try again later.\"}",
                    Instant.now().toString()
            );
            response.getWriter().write(body);
        }
    }

    private String resolveKey(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {
            return authentication.getName();
        }
        return request.getRemoteAddr();
    }

    private Bucket createBucket() {
        return Bucket.builder()
                .addLimit(limit -> limit.capacity(capacity)
                        .refillGreedy(capacity, Duration.ofMinutes(refillPeriodMinutes)))
                .build();
    }

    private void evictExpiredEntries() {
        long now = System.currentTimeMillis();
        long expirationMillis = TimeUnit.MINUTES.toMillis(expirationMinutes);
        buckets.entrySet().removeIf(entry -> now - entry.getValue().getLastAccessMillis() > expirationMillis);
    }

    @PreDestroy
    public void shutdown() {
        scheduler.shutdownNow();
    }

    private static class TimestampedBucket {
        private final Bucket bucket;
        private volatile long lastAccessMillis;

        TimestampedBucket(Bucket bucket) {
            this.bucket = bucket;
            this.lastAccessMillis = System.currentTimeMillis();
        }

        Bucket getBucket() {
            return bucket;
        }

        long getLastAccessMillis() {
            return lastAccessMillis;
        }

        void touch() {
            this.lastAccessMillis = System.currentTimeMillis();
        }
    }
}
