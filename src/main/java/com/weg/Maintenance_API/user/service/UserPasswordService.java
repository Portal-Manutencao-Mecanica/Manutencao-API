package com.weg.Maintenance_API.user.service;

import com.weg.Maintenance_API.audit.service.AuditService;
import com.weg.Maintenance_API.auth.service.ClientRequestMetadata;
import com.weg.Maintenance_API.auth.service.RefreshTokenService;
import com.weg.Maintenance_API.exception.type.InvalidRequestException;
import com.weg.Maintenance_API.exception.type.ResourceNotFoundException;
import com.weg.Maintenance_API.user.UserRepository;
import com.weg.Maintenance_API.user.dto.request.ChangeOwnPasswordRequest;
import com.weg.Maintenance_API.user.entity.User;
import com.weg.Maintenance_API.user.event.PasswordChangedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserPasswordService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordPolicyValidator passwordPolicyValidator;
    private final RefreshTokenService refreshTokenService;
    private final AuditService auditService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void changeOwnPassword(
            String email,
            ChangeOwnPasswordRequest request,
            ClientRequestMetadata metadata
    ) {
        User user = userRepository.findByEmailForUpdate(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário autenticado"));
        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new InvalidRequestException("A senha atual está incorreta.");
        }
        passwordPolicyValidator.validate(
                request.newPassword(),
                request.passwordConfirmation()
        );
        if (passwordEncoder.matches(request.newPassword(), user.getPassword())) {
            throw new InvalidRequestException(
                    "A nova senha deve ser diferente da senha atual."
            );
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        user.setPasswordChangeRequired(false);
        user.setTemporaryPasswordExpiresAt(null);
        user.setPasswordChangedAt(LocalDateTime.now());
        refreshTokenService.revokeAll(user.getId());

        auditService.recordInCurrentTransaction(
                user,
                "PASSWORD_CHANGED",
                "USER",
                user.getId(),
                metadata.endpoint(),
                metadata.httpMethod(),
                metadata.ipAddress(),
                metadata.userAgent(),
                true,
                "Senha alterada pelo próprio usuário."
        );
        eventPublisher.publishEvent(new PasswordChangedEvent(
                user.getId(),
                user.getName(),
                user.getEmail()
        ));
    }
}
