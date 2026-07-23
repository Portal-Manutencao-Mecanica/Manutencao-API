package com.weg.Maintenance_API.config.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LoginRateLimitFilter extends OncePerRequestFilter {

    private final int requestsPerWindow;
    private final Duration window;
    private final ConcurrentMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    public LoginRateLimitFilter(
            @Value("${app.security.login-rate-limit.capacity:5}") int requestsPerWindow,
            @Value("${app.security.login-rate-limit.refill-seconds:60}") long refillSeconds
    ) {
        if (requestsPerWindow <= 0 || refillSeconds <= 0) {
            throw new IllegalArgumentException("Login rate limit values must be positive");
        }

        this.requestsPerWindow = requestsPerWindow;
        this.window = Duration.ofSeconds(refillSeconds);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !HttpMethod.POST.matches(request.getMethod())
                || !request.getRequestURI().endsWith("/auth/login");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        Bucket bucket = buckets.computeIfAbsent(
                request.getRemoteAddr(),
                ignored -> createBucket()
        );

        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (!probe.isConsumed()) {
            long retryAfterSeconds = Math.max(
                    1,
                    TimeUnit.NANOSECONDS.toSeconds(
                            probe.getNanosToWaitForRefill()
                    )
            );

            response.setStatus(429);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setHeader("Retry-After", String.valueOf(retryAfterSeconds));
            response.getWriter().write(
                    """
                    {"status":429,"error":"RATE_LIMIT_EXCEEDED",\
                    "message":"Muitas tentativas de login. Aguarde e tente novamente.",\
                    "path":"%s","timestamp":"%s","errors":{}}
                    """.formatted(request.getRequestURI(), LocalDateTime.now()).trim()
            );
            return;
        }

        filterChain.doFilter(request, response);
    }

    private Bucket createBucket() {
        Bandwidth limit = Bandwidth.builder()
                .capacity(requestsPerWindow)
                .refillGreedy(requestsPerWindow, window)
                .build();

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}
