package com.weg.Maintenance_API.auth.service;

import com.weg.Maintenance_API.audit.service.AuditService;
import com.weg.Maintenance_API.user.UserRepository;
import com.weg.Maintenance_API.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 5;

    private final UserRepository userRepository;
    private final AuditService auditService;

    // Executa o fluxo de comunicacao ou registro.
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordFailure(String identifier, ClientRequestMetadata metadata) {
        String normalizedIdentifier = normalize(identifier);
        User user = userRepository.findByLoginIdentifierForUpdate(normalizedIdentifier).orElse(null);

        if (user != null && user.isEnabled() && user.isAccountNonLocked()) {
            LocalDateTime now = LocalDateTime.now();
            user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
            user.setLastFailedLoginAt(now);

            if (user.getFailedLoginAttempts() >= MAX_ATTEMPTS) {
                int nextLockoutCount = user.getLockoutCount() + 1;
                user.setLockoutCount(nextLockoutCount);
                user.setFailedLoginAttempts(0);
                user.setLockedUntil(now.plus(lockDuration(nextLockoutCount)));
            }
            userRepository.save(user);
        }

        if (user == null) {
            auditService.recordAnonymous(
                    normalizedIdentifier,
                    "LOGIN_FAILURE",
                    metadata.endpoint(),
                    metadata.httpMethod(),
                    metadata.ipAddress(),
                    metadata.userAgent(),
                    false,
                    "Credenciais invÃ¡lidas."
            );
        } else {
            auditService.record(
                    user,
                    "LOGIN_FAILURE",
                    "USER",
                    user.getId(),
                    metadata.endpoint(),
                    metadata.httpMethod(),
                    metadata.ipAddress(),
                    metadata.userAgent(),
                    false,
                    user.isTemporarilyLocked()
                            ? "Conta bloqueada temporariamente apÃ³s tentativas invÃ¡lidas."
                            : "Credenciais invÃ¡lidas."
            );
        }
    }

    // Executa o fluxo de comunicacao ou registro.
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordSuccess(UUID userId) {
        userRepository.findByIdForUpdate(userId).ifPresent(user -> {
            user.setFailedLoginAttempts(0);
            user.setLockedUntil(null);
            user.setLastFailedLoginAt(null);
            userRepository.save(user);
        });
    }

    // Executa a operacao deste metodo.
    private Duration lockDuration(int lockoutCount) {
        return switch (lockoutCount) {
            case 1 -> Duration.ofMinutes(5);
            case 2 -> Duration.ofMinutes(15);
            default -> Duration.ofHours(1);
        };
    }

    // Converte os dados para o formato necessario.
    private String normalize(String identifier) {
        return identifier == null ? "" : identifier.trim().toLowerCase(Locale.ROOT);
    }
}
