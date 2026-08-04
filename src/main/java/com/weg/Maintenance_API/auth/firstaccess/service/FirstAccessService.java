package com.weg.Maintenance_API.auth.firstaccess.service;

import com.weg.Maintenance_API.audit.service.AuditService;
import com.weg.Maintenance_API.auth.firstaccess.dto.CompleteFirstAccessRequest;
import com.weg.Maintenance_API.auth.firstaccess.entity.FirstAccessCode;
import com.weg.Maintenance_API.auth.firstaccess.event.FirstAccessCodeRequestedEvent;
import com.weg.Maintenance_API.auth.firstaccess.repository.FirstAccessCodeRepository;
import com.weg.Maintenance_API.auth.service.ClientRequestMetadata;
import com.weg.Maintenance_API.auth.service.RefreshTokenService;
import com.weg.Maintenance_API.exception.type.ExpiredTokenException;
import com.weg.Maintenance_API.exception.type.InvalidRequestException;
import com.weg.Maintenance_API.exception.type.InvalidTokenException;
import com.weg.Maintenance_API.exception.type.RateLimitExceededException;
import com.weg.Maintenance_API.exception.type.ResourceNotFoundException;
import com.weg.Maintenance_API.user.UserRepository;
import com.weg.Maintenance_API.user.entity.User;
import com.weg.Maintenance_API.user.event.PasswordChangedEvent;
import com.weg.Maintenance_API.user.service.PasswordPolicyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class FirstAccessService {

    private final UserRepository userRepository;
    private final FirstAccessCodeRepository codeRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordPolicyValidator passwordPolicyValidator;
    private final RefreshTokenService refreshTokenService;
    private final AuditService auditService;
    private final ApplicationEventPublisher eventPublisher;
    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${app.first-access-code.expiration-seconds:600}")
    private long expirationSeconds;

    @Value("${app.first-access-code.resend-cooldown-seconds:60}")
    private long resendCooldownSeconds;

    @Value("${app.first-access-code.max-attempts:5}")
    private int maxAttempts;

    @Transactional
    public void requestCode(String email, ClientRequestMetadata metadata) {
        User user = requiredPendingUser(email);
        LocalDateTime now = LocalDateTime.now();
        codeRepository.findFirstByUserIdOrderByCreatedAtDesc(user.getId())
                .filter(code -> code.getCreatedAt().isAfter(now.minusSeconds(resendCooldownSeconds)))
                .ifPresent(code -> {
                    throw new RateLimitExceededException(
                            "Aguarde antes de solicitar um novo código."
                    );
                });

        codeRepository.invalidateAllActiveByUserId(user.getId(), now);
        String code = String.format(
                Locale.ROOT,
                "%06d",
                secureRandom.nextInt(1_000_000)
        );
        codeRepository.save(new FirstAccessCode(
                user,
                passwordEncoder.encode(code),
                now.plusSeconds(expirationSeconds)
        ));

        auditService.recordInCurrentTransaction(
                user,
                "FIRST_ACCESS_CODE_REQUESTED",
                "USER",
                user.getId(),
                metadata.endpoint(),
                metadata.httpMethod(),
                metadata.ipAddress(),
                metadata.userAgent(),
                true,
                "Código de verificação do primeiro acesso enviado."
        );
        eventPublisher.publishEvent(new FirstAccessCodeRequestedEvent(
                user.getId(),
                user.getName(),
                user.getEmail(),
                code
        ));
    }

    @Transactional(noRollbackFor = {
            InvalidTokenException.class,
            ExpiredTokenException.class
    })
    public void complete(
            String email,
            CompleteFirstAccessRequest request,
            ClientRequestMetadata metadata
    ) {
        User user = requiredPendingUser(email);
        FirstAccessCode code = codeRepository
                .findFirstByUserIdAndUsedAtIsNullOrderByCreatedAtDesc(user.getId())
                .orElseThrow(() -> new InvalidTokenException(
                        "Solicite um código de verificação antes de continuar."
                ));
        LocalDateTime now = LocalDateTime.now();

        if (code.isExpired(now)) {
            code.use(now);
            throw new ExpiredTokenException(
                    "O código de verificação expirou. Solicite um novo."
            );
        }
        if (!passwordEncoder.matches(request.code(), code.getCodeHash())) {
            if (code.registerFailedAttempt() >= maxAttempts) {
                code.use(now);
            }
            throw new InvalidTokenException("O código de verificação é inválido.");
        }

        passwordPolicyValidator.validate(
                request.newPassword(),
                request.passwordConfirmation()
        );
        if (passwordEncoder.matches(request.newPassword(), user.getPassword())) {
            throw new InvalidRequestException(
                    "A nova senha deve ser diferente da senha temporária."
            );
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        user.setPasswordChangeRequired(false);
        user.setTemporaryPasswordExpiresAt(null);
        user.setPasswordChangedAt(now);
        user.setSecurityVersion(user.getSecurityVersion() + 1);
        code.use(now);
        codeRepository.invalidateAllActiveByUserId(user.getId(), now);
        refreshTokenService.revokeAll(user.getId());

        auditService.recordInCurrentTransaction(
                user,
                "FIRST_ACCESS_COMPLETED",
                "USER",
                user.getId(),
                metadata.endpoint(),
                metadata.httpMethod(),
                metadata.ipAddress(),
                metadata.userAgent(),
                true,
                "Senha definitiva cadastrada com verificação por código."
        );
        eventPublisher.publishEvent(new PasswordChangedEvent(
                user.getId(),
                user.getName(),
                user.getEmail()
        ));
    }

    private User requiredPendingUser(String email) {
        User user = userRepository.findByEmailForUpdate(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário autenticado"));
        if (!user.isPasswordChangeRequired()) {
            throw new InvalidRequestException(
                    "O primeiro acesso já foi concluído para esta conta."
            );
        }
        return user;
    }
}
