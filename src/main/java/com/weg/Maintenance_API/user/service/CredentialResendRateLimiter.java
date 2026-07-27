package com.weg.Maintenance_API.user.service;

import com.weg.Maintenance_API.exception.type.RateLimitExceededException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CredentialResendRateLimiter {

    private final int capacity;
    private final Duration window;
    private final Map<String, Deque<Instant>> attempts = new ConcurrentHashMap<>();

    public CredentialResendRateLimiter(
            @Value("${app.credentials-resend.rate-limit.capacity:3}") int capacity,
            @Value("${app.credentials-resend.rate-limit.window-seconds:3600}")
            long windowSeconds
    ) {
        if (capacity <= 0 || windowSeconds <= 0) {
            throw new IllegalArgumentException(
                    "Os limites de reenvio de credenciais devem ser positivos."
            );
        }
        this.capacity = capacity;
        this.window = Duration.ofSeconds(windowSeconds);
    }

    public void check(UUID actorId, UUID targetId) {
        String key = actorId + ":" + targetId;
        Instant now = Instant.now();
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
                throw new RateLimitExceededException(
                        "Muitos reenvios de credenciais. Aguarde antes de tentar novamente."
                );
            }
            values.addLast(now);
        }
    }
}
