package com.weg.Maintenance_API.user.service;

import com.weg.Maintenance_API.audit.service.AuditService;
import com.weg.Maintenance_API.auth.service.ClientRequestMetadata;
import com.weg.Maintenance_API.auth.service.RefreshTokenService;
import com.weg.Maintenance_API.enums.Role;
import com.weg.Maintenance_API.exception.type.InvalidStateException;
import com.weg.Maintenance_API.exception.type.ResourceNotFoundException;
import com.weg.Maintenance_API.organization.dto.OrganizationSummaryResponse;
import com.weg.Maintenance_API.user.UserRepository;
import com.weg.Maintenance_API.user.dto.response.CredentialResendResponse;
import com.weg.Maintenance_API.user.dto.response.ManagedUserResponse;
import com.weg.Maintenance_API.user.entity.User;
import com.weg.Maintenance_API.user.event.TemporaryCredentialsReissuedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserAdministrationService {

    private final UserRepository userRepository;
    private final UserManagementPermissionService permissionService;
    private final RefreshTokenService refreshTokenService;
    private final TemporaryCredentialService temporaryCredentialService;
    private final CredentialResendRateLimiter credentialResendRateLimiter;
    private final UserRolePersistenceService rolePersistenceService;
    private final AuditService auditService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public ManagedUserResponse block(
            UUID userId,
            String reason,
            String actorEmail,
            ClientRequestMetadata metadata
    ) {
        User actor = actor(actorEmail);
        User target = target(userId);
        permissionService.validateCanManage(actor, target);
        validateNotSelf(actor, target, "bloquear");
        if (!target.isEnabled()) {
            throw new InvalidStateException(
                    "O usuário está inativo. Reative-o antes de alterar o bloqueio."
            );
        }
        if (!target.isAccountNonLocked()) {
            throw new InvalidStateException("O usuário já está bloqueado.");
        }
        protectLastActiveAdmin(target);

        target.setAccountNonLocked(false);
        target.setLockedUntil(null);
        incrementSecurityVersion(target);
        applyStatusMetadata(target, actor, reason);
        refreshTokenService.revokeAll(target.getId());
        userRepository.saveAndFlush(target);
        audit(actor, target, "USER_BLOCKED", reason, metadata);
        return response(target);
    }

    @Transactional
    public ManagedUserResponse unblock(
            UUID userId,
            String reason,
            String actorEmail,
            ClientRequestMetadata metadata
    ) {
        User actor = actor(actorEmail);
        User target = target(userId);
        permissionService.validateCanManage(actor, target);
        validateNotSelf(actor, target, "desbloquear");
        if (target.isAccountNonLocked()) {
            throw new InvalidStateException("O usuário não está bloqueado.");
        }

        target.setAccountNonLocked(true);
        target.setLockedUntil(null);
        target.setFailedLoginAttempts(0);
        target.setLockoutCount(0);
        incrementSecurityVersion(target);
        applyStatusMetadata(target, actor, reason);
        userRepository.saveAndFlush(target);
        audit(actor, target, "USER_UNBLOCKED", reason, metadata);
        return response(target);
    }

    @Transactional
    public ManagedUserResponse deactivate(
            UUID userId,
            String reason,
            String actorEmail,
            ClientRequestMetadata metadata
    ) {
        User actor = actor(actorEmail);
        User target = target(userId);
        permissionService.validateCanManage(actor, target);
        validateNotSelf(actor, target, "inativar");
        if (!target.isEnabled()) {
            throw new InvalidStateException("O usuário já está inativo.");
        }
        protectLastActiveAdmin(target);

        target.setEnabled(false);
        incrementSecurityVersion(target);
        applyStatusMetadata(target, actor, reason);
        refreshTokenService.revokeAll(target.getId());
        userRepository.saveAndFlush(target);
        audit(actor, target, "USER_DEACTIVATED", reason, metadata);
        return response(target);
    }

    @Transactional
    public ManagedUserResponse reactivate(
            UUID userId,
            String reason,
            String actorEmail,
            ClientRequestMetadata metadata
    ) {
        User actor = actor(actorEmail);
        User target = target(userId);
        permissionService.validateCanManage(actor, target);
        validateNotSelf(actor, target, "reativar");
        if (target.isEnabled()) {
            throw new InvalidStateException("O usuário já está ativo.");
        }

        target.setEnabled(true);
        incrementSecurityVersion(target);
        applyStatusMetadata(target, actor, reason);
        userRepository.saveAndFlush(target);
        audit(actor, target, "USER_REACTIVATED", reason, metadata);
        return response(target);
    }

    @Transactional
    public ManagedUserResponse changeRole(
            UUID userId,
            Role targetRole,
            String actorEmail,
            ClientRequestMetadata metadata
    ) {
        User actor = actor(actorEmail);
        User target = target(userId);
        permissionService.validateCanChangeRole(actor);
        validateNotSelf(actor, target, "alterar a role de");
        if (!target.isEnabled() || !target.isAccountNonLocked()) {
            throw new InvalidStateException(
                    "Reative e desbloqueie o usuário antes de alterar seus privilégios."
            );
        }
        Role previousRole = target.getRole();
        if (previousRole == targetRole) {
            throw new InvalidStateException("O usuário já possui a role informada.");
        }
        protectLastActiveAdmin(target);

        refreshTokenService.revokeAll(target.getId());
        rolePersistenceService.transition(target.getId(), previousRole, targetRole);
        User changed = userRepository.findById(target.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário"));
        auditService.recordInCurrentTransaction(
                actor,
                "USER_ROLE_CHANGED",
                "USER",
                changed.getId(),
                metadata.endpoint(),
                metadata.httpMethod(),
                metadata.ipAddress(),
                metadata.userAgent(),
                true,
                "Role anterior: " + previousRole + "; nova role: " + targetRole
        );
        return response(changed);
    }

    @Transactional
    public CredentialResendResponse resendCredentials(
            UUID userId,
            String actorEmail,
            ClientRequestMetadata metadata
    ) {
        User actor = actor(actorEmail);
        User target = target(userId);
        permissionService.validateCanResendCredentials(actor, target);
        if (!target.isEnabled() || !target.isAccountNonLocked()) {
            throw new InvalidStateException(
                    "As credenciais só podem ser reenviadas para um usuário ativo e desbloqueado."
            );
        }
        credentialResendRateLimiter.check(actor.getId(), target.getId());

        String temporaryPassword = temporaryCredentialService.issue(target);
        incrementSecurityVersion(target);
        refreshTokenService.revokeAll(target.getId());
        userRepository.saveAndFlush(target);
        audit(actor, target, "USER_CREDENTIALS_REISSUED", null, metadata);
        eventPublisher.publishEvent(new TemporaryCredentialsReissuedEvent(
                target.getId(),
                target.getName(),
                target.getEmail(),
                temporaryPassword
        ));
        return new CredentialResendResponse(
                target.getId(),
                false,
                "PENDING",
                "O envio das novas credenciais foi agendado."
        );
    }

    private User actor(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário autenticado"));
    }

    private User target(UUID userId) {
        return userRepository.findByIdForUpdate(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário"));
    }

    private void validateNotSelf(User actor, User target, String action) {
        if (actor.getId().equals(target.getId())) {
            throw new InvalidStateException(
                    "O usuário não pode " + action + " a própria conta."
            );
        }
    }

    private void protectLastActiveAdmin(User target) {
        if (target.getRole() == Role.ADMIN
                && target.isEnabled()
                && target.isAccountNonLocked()
                && userRepository.countByRoleAndEnabledTrueAndAccountNonLockedTrue(
                        Role.ADMIN
                ) <= 1) {
            throw new InvalidStateException(
                    "A operação removeria o último administrador ativo."
            );
        }
    }

    private void applyStatusMetadata(User target, User actor, String reason) {
        target.setStatusChangeReason(reason.trim());
        target.setStatusChangedAt(LocalDateTime.now());
        target.setStatusChangedBy(actor.getId());
    }

    private void incrementSecurityVersion(User user) {
        user.setSecurityVersion(user.getSecurityVersion() + 1);
    }

    private void audit(
            User actor,
            User target,
            String action,
            String reason,
            ClientRequestMetadata metadata
    ) {
        String details = reason == null
                ? "Operação administrativa concluída."
                : "Motivo: " + reason.trim();
        auditService.recordInCurrentTransaction(
                actor,
                action,
                "USER",
                target.getId(),
                metadata.endpoint(),
                metadata.httpMethod(),
                metadata.ipAddress(),
                metadata.userAgent(),
                true,
                details
        );
    }

    private ManagedUserResponse response(User user) {
        return new ManagedUserResponse(
                user.getId(),
                user.getName(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getStatus(),
                user.isPasswordChangeRequired(),
                new OrganizationSummaryResponse(
                        user.getOrganization().getId(),
                        user.getOrganization().getName()
                ),
                user.getStatusChangeReason(),
                user.getStatusChangedAt(),
                user.getStatusChangedBy(),
                user.getUpdatedAt()
        );
    }
}
