package com.weg.Maintenance_API.auth.password.service;

import com.weg.Maintenance_API.audit.service.AuditService;
import com.weg.Maintenance_API.auth.password.dto.ResetPasswordRequest;
import com.weg.Maintenance_API.auth.password.entity.PasswordResetToken;
import com.weg.Maintenance_API.auth.password.event.PasswordResetRequestedEvent;
import com.weg.Maintenance_API.auth.password.repository.PasswordResetTokenRepository;
import com.weg.Maintenance_API.auth.service.ClientRequestMetadata;
import com.weg.Maintenance_API.auth.service.RefreshTokenService;
import com.weg.Maintenance_API.auth.service.SecureTokenService;
import com.weg.Maintenance_API.exception.type.ExpiredTokenException;
import com.weg.Maintenance_API.exception.type.InvalidTokenException;
import com.weg.Maintenance_API.user.UserRepository;
import com.weg.Maintenance_API.user.entity.User;
import com.weg.Maintenance_API.user.event.PasswordChangedEvent;
import com.weg.Maintenance_API.user.service.PasswordPolicyValidator;
import com.weg.Maintenance_API.user.service.UserIdentityPolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PasswordResetTokenService {

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final SecureTokenService secureTokenService;
    private final UserIdentityPolicy userIdentityPolicy;
    private final PasswordRecoveryRateLimiter rateLimiter;
    private final PasswordPolicyValidator passwordPolicyValidator;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final AuditService auditService;
    private final ApplicationEventPublisher eventPublisher;

    @Value("${app.password-recovery.expiration-seconds:1800}")
    private long expirationSeconds;

    @Transactional
    public void requestReset(String rawEmail, ClientRequestMetadata metadata) {
        String email = userIdentityPolicy.normalizeEmail(rawEmail);
        rateLimiter.check(metadata.ipAddress(), email);
        User user = userRepository.findByEmailIgnoreCase(email).orElse(null);
        if (user == null
                || !user.isEnabled()
                || !user.isAccountNonLocked()
                || !user.getOrganization().isActive()) {
            auditService.recordAnonymous(
                    email,
                    "PASSWORD_RESET_REQUESTED",
                    metadata.endpoint(),
                    metadata.httpMethod(),
                    metadata.ipAddress(),
                    metadata.userAgent(),
                    true,
                    "Solicitação processada sem divulgação do cadastro."
            );
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        tokenRepository.invalidateAllActiveByUserId(user.getId(), now);
        String rawToken = secureTokenService.generate();
        tokenRepository.save(new PasswordResetToken(
                user,
                secureTokenService.hash(rawToken),
                now.plusSeconds(expirationSeconds),
                metadata.ipAddress()
        ));
        auditService.recordInCurrentTransaction(
                user,
                "PASSWORD_RESET_REQUESTED",
                "USER",
                user.getId(),
                metadata.endpoint(),
                metadata.httpMethod(),
                metadata.ipAddress(),
                metadata.userAgent(),
                true,
                "Token temporário de recuperação gerado."
        );
        eventPublisher.publishEvent(new PasswordResetRequestedEvent(
                user.getId(),
                user.getName(),
                user.getEmail(),
                rawToken
        ));
    }

    @Transactional(readOnly = true)
    public boolean validate(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            return false;
        }
        return tokenRepository.findByTokenHash(secureTokenService.hash(rawToken))
                .filter(token -> !token.isUsed())
                .filter(token -> !token.isExpired(LocalDateTime.now()))
                .isPresent();
    }

    @Transactional
    public void reset(
            ResetPasswordRequest request,
            ClientRequestMetadata metadata
    ) {
        PasswordResetToken token = tokenRepository.findByTokenHashForUpdate(
                        secureTokenService.hash(request.token())
                )
                .orElseThrow(() -> new InvalidTokenException(
                        "O token de recuperação é inválido."
                ));
        LocalDateTime now = LocalDateTime.now();
        if (token.isUsed()) {
            throw new InvalidTokenException(
                    "O token de recuperação já foi utilizado ou invalidado."
            );
        }
        if (token.isExpired(now)) {
            token.use(now);
            throw new ExpiredTokenException(
                    "O token de recuperação expirou. Solicite um novo."
            );
        }

        passwordPolicyValidator.validate(
                request.newPassword(),
                request.passwordConfirmation()
        );
        User user = token.getUser();
        if (passwordEncoder.matches(request.newPassword(), user.getPassword())) {
            throw new com.weg.Maintenance_API.exception.type.InvalidRequestException(
                    "A nova senha deve ser diferente da senha atual."
            );
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        user.setPasswordChangedAt(now);
        user.setPasswordChangeRequired(false);
        user.setTemporaryPasswordExpiresAt(null);
        user.setSecurityVersion(user.getSecurityVersion() + 1);
        token.use(now);
        refreshTokenService.revokeAll(user.getId());
        auditService.recordInCurrentTransaction(
                user,
                "PASSWORD_RESET_COMPLETED",
                "USER",
                user.getId(),
                metadata.endpoint(),
                metadata.httpMethod(),
                metadata.ipAddress(),
                metadata.userAgent(),
                true,
                "Senha redefinida com token de uso único."
        );
        eventPublisher.publishEvent(new PasswordChangedEvent(
                user.getId(),
                user.getName(),
                user.getEmail()
        ));
    }
}
