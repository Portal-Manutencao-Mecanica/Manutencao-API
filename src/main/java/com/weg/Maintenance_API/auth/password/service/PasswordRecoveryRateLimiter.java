package com.weg.Maintenance_API.auth.password.service;

import com.weg.Maintenance_API.exception.type.RateLimitExceededException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PasswordRecoveryRateLimiter {

    private final int ipCapacity;
    private final int emailCapacity;
    private final Duration window;
    private final Map<String, Deque<Instant>> ipAttempts = new ConcurrentHashMap<>();
    private final Map<String, Deque<Instant>> emailAttempts = new ConcurrentHashMap<>();

    public PasswordRecoveryRateLimiter(
            @Value("${app.password-recovery.rate-limit.ip-capacity:10}") int ipCapacity,
            @Value("${app.password-recovery.rate-limit.email-capacity:3}") int emailCapacity,
            @Value("${app.password-recovery.rate-limit.window-seconds:3600}") long windowSeconds
    ) {
        if (ipCapacity <= 0 || emailCapacity <= 0 || windowSeconds <= 0) {
            throw new IllegalArgumentException(
                    "Os limites de recuperação de senha devem ser positivos."
            );
        }
        this.ipCapacity = ipCapacity;
        this.emailCapacity = emailCapacity;
        this.window = Duration.ofSeconds(windowSeconds);
    }

    public void check(String ipAddress, String email) {
        Instant now = Instant.now();
        if (!acquire(ipAttempts, "ip:" + ipAddress, ipCapacity, now)
                || !acquire(emailAttempts, "email:" + email, emailCapacity, now)) {
            throw new RateLimitExceededException(
                    "Muitas solicitações de recuperação. Aguarde antes de tentar novamente."
            );
        }
    }

    private boolean acquire(
            Map<String, Deque<Instant>> attempts,
            String key,
            int capacity,
            Instant now
    ) {
        Deque<Instant> values = attempts.computeIfAbsent(
                key,
                ignored -> new ArrayDeque<>()
        );
        synchronized (values) {
            Instant cutoff = now.minus(window);
            while (!values.isEmpty() && values.peekFirst().isBefore(cutoff)) {
                values.removeFirst();
            }
            if (values.size() >= capacity) {
                return false;
            }
            values.addLast(now);
            return true;
        }
    }
}
