package com.weg.Maintenance_API.user.service;

import com.weg.Maintenance_API.audit.service.AuditService;
import com.weg.Maintenance_API.auth.service.ClientRequestMetadata;
import com.weg.Maintenance_API.auth.service.RefreshTokenService;
import com.weg.Maintenance_API.enums.Role;
import com.weg.Maintenance_API.exception.type.ConflictException;
import com.weg.Maintenance_API.exception.type.InvalidRequestException;
import com.weg.Maintenance_API.exception.type.InvalidStateException;
import com.weg.Maintenance_API.exception.type.ResourceNotFoundException;
import com.weg.Maintenance_API.organization.entity.Organization;
import com.weg.Maintenance_API.organization.repository.OrganizationRepository;
import com.weg.Maintenance_API.organization.dto.OrganizationSummaryResponse;
import com.weg.Maintenance_API.user.UserRepository;
import com.weg.Maintenance_API.user.dto.response.CredentialResendResponse;
import com.weg.Maintenance_API.user.dto.response.ManagedUserResponse;
import com.weg.Maintenance_API.user.dto.request.UpdateUserRequest;
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
    private final UserIdentityPolicy userIdentityPolicy;
    private final OrganizationRepository organizationRepository;
    private final AuditService auditService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<ManagedUserResponse> getAll(
            String search,
            Role role,
            Boolean enabled,
            org.springframework.data.domain.Pageable pageable,
            String actorEmail
    ) {
        User actor = actor(actorEmail);
        org.springframework.data.jpa.domain.Specification<User> filters =
                (root, query, builder) -> actor.getRole() == Role.COORDENADOR
                        ? root.get("role").in(Role.ALUNO, Role.PROFESSOR)
                        : builder.conjunction();

        if (search != null && !search.isBlank()) {
            String pattern = "%" + search.trim().toLowerCase(java.util.Locale.ROOT) + "%";
            filters = filters.and((root, query, builder) -> builder.or(
                    builder.like(builder.lower(root.get("name")), pattern),
                    builder.like(builder.lower(root.get("email")), pattern),
                    builder.like(builder.lower(root.get("username")), pattern),
                    builder.like(builder.lower(root.get("numberCard")), pattern)
            ));
        }
        if (role != null) {
            filters = filters.and((root, query, builder) -> builder.equal(root.get("role"), role));
        }
        if (enabled != null) {
            filters = filters.and((root, query, builder) ->
                    builder.equal(root.get("enabled"), enabled));
        }

        return userRepository.findAll(filters, pageable).map(this::response);
    }

    @Transactional(readOnly = true)
    public ManagedUserResponse getById(UUID userId, String actorEmail) {
        User actor = actor(actorEmail);
        User target = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", userId));
        permissionService.validateCanManage(actor, target);
        return response(target);
    }

    @Transactional
    public ManagedUserResponse update(
            UUID userId,
            UpdateUserRequest request,
            String actorEmail,
            ClientRequestMetadata metadata
    ) {
        User actor = actor(actorEmail);
        User target = target(userId);
        permissionService.validateCanManage(actor, target);

        String name = request.name().trim();
        String email = userIdentityPolicy.normalizeEmail(request.email());
        String numberCard = request.numberCard().trim();
        userIdentityPolicy.validateName(name);
        userIdentityPolicy.validateEmail(email);

        if (userRepository.existsByEmailIgnoreCaseAndIdNot(email, target.getId())) {
            throw new ConflictException("O e-mail informado ja esta cadastrado.");
        }
        if (userRepository.existsByNumberCardIgnoreCaseAndIdNot(numberCard, target.getId())) {
            throw new ConflictException("O numero do cracha informado ja esta cadastrado.");
        }

        Organization organization = request.organizationId() == null
                ? target.getOrganization()
                : organizationRepository.findById(request.organizationId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Organizacao",
                                request.organizationId()
                        ));
        if (!organization.isActive()) {
            throw new InvalidRequestException("A organizacao selecionada esta inativa.");
        }
        if (!organization.acceptsEmail(email)) {
            throw new InvalidRequestException(
                    "O dominio do e-mail nao corresponde a organizacao selecionada."
            );
        }

        boolean authenticationDataChanged = !target.getEmail().equalsIgnoreCase(email)
                || !target.getOrganization().getId().equals(organization.getId());
        target.setName(name);
        target.setEmail(email);
        target.setNumberCard(numberCard);
        target.setOrganization(organization);
        if (authenticationDataChanged) {
            incrementSecurityVersion(target);
            refreshTokenService.revokeAll(target.getId());
        }
        userRepository.saveAndFlush(target);
        auditService.recordInCurrentTransaction(
                actor,
                "USER_UPDATED",
                "USER",
                target.getId(),
                metadata.endpoint(),
                metadata.httpMethod(),
                metadata.ipAddress(),
                metadata.userAgent(),
                true,
                "Dados cadastrais atualizados pelo administrador."
        );
        return response(target);
    }

    // Executa a operacao deste metodo.
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
                    "O usuÃ¡rio estÃ¡ inativo. Reative-o antes de alterar o bloqueio."
            );
        }
        if (!target.isAccountNonLocked()) {
            throw new InvalidStateException("O usuÃ¡rio jÃ¡ estÃ¡ bloqueado.");
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

    // Executa a operacao deste metodo.
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
            throw new InvalidStateException("O usuÃ¡rio nÃ£o estÃ¡ bloqueado.");
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

    // Atualiza o estado conforme os dados informados.
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
            throw new InvalidStateException("O usuÃ¡rio jÃ¡ estÃ¡ inativo.");
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

    // Executa a operacao deste metodo.
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
            throw new InvalidStateException("O usuÃ¡rio jÃ¡ estÃ¡ ativo.");
        }

        target.setEnabled(true);
        incrementSecurityVersion(target);
        applyStatusMetadata(target, actor, reason);
        userRepository.saveAndFlush(target);
        audit(actor, target, "USER_REACTIVATED", reason, metadata);
        return response(target);
    }

    // Atualiza o estado conforme os dados informados.
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
                    "Reative e desbloqueie o usuÃ¡rio antes de alterar seus privilÃ©gios."
            );
        }
        Role previousRole = target.getRole();
        if (previousRole == targetRole) {
            throw new InvalidStateException("O usuÃ¡rio jÃ¡ possui a role informada.");
        }
        protectLastActiveAdmin(target);

        refreshTokenService.revokeAll(target.getId());
        rolePersistenceService.transition(target.getId(), previousRole, targetRole);
        User changed = userRepository.findById(target.getId())
                .orElseThrow(() -> new ResourceNotFoundException("UsuÃ¡rio"));
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

    // Executa a operacao deste metodo.
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
                    "As credenciais sÃ³ podem ser reenviadas para um usuÃ¡rio ativo e desbloqueado."
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

    // Executa a operacao deste metodo.
    private User actor(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("UsuÃ¡rio autenticado"));
    }

    // Executa a operacao deste metodo.
    private User target(UUID userId) {
        return userRepository.findByIdForUpdate(userId)
                .orElseThrow(() -> new ResourceNotFoundException("UsuÃ¡rio"));
    }

    // Valida a regra aplicada por este metodo.
    private void validateNotSelf(User actor, User target, String action) {
        if (actor.getId().equals(target.getId())) {
            throw new InvalidStateException(
                    "O usuÃ¡rio nÃ£o pode " + action + " a prÃ³pria conta."
            );
        }
    }

    // Executa a operacao deste metodo.
    private void protectLastActiveAdmin(User target) {
        if (target.getRole() == Role.ADMIN
                && target.isEnabled()
                && target.isAccountNonLocked()
                && userRepository.countByRoleAndEnabledTrueAndAccountNonLockedTrue(
                        Role.ADMIN
                ) <= 1) {
            throw new InvalidStateException(
                    "A operaÃ§Ã£o removeria o Ãºltimo administrador ativo."
            );
        }
    }

    // Aplica os dados recebidos na entidade.
    private void applyStatusMetadata(User target, User actor, String reason) {
        target.setStatusChangeReason(reason.trim());
        target.setStatusChangedAt(LocalDateTime.now());
        target.setStatusChangedBy(actor.getId());
    }

    // Executa a operacao deste metodo.
    private void incrementSecurityVersion(User user) {
        user.setSecurityVersion(user.getSecurityVersion() + 1);
    }

    // Executa a operacao deste metodo.
    private void audit(
            User actor,
            User target,
            String action,
            String reason,
            ClientRequestMetadata metadata
    ) {
        String details = reason == null
                ? "OperaÃ§Ã£o administrativa concluÃ­da."
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

    // Executa a operacao deste metodo.
    private ManagedUserResponse response(User user) {
        return new ManagedUserResponse(
                user.getId(),
                user.getName(),
                user.getUsername(),
                user.getEmail(),
                user.getNumberCard(),
                user.getRole(),
                user.getStatus(),
                user.isPasswordChangeRequired(),
                new OrganizationSummaryResponse(
                        user.getOrganization().getId(),
                        user.getOrganization().getName()
                ),
                user.isEnabled(),
                user.isAccountNonLocked(),
                user.getStatusChangeReason(),
                user.getStatusChangedAt(),
                user.getStatusChangedBy(),
                user.getUpdatedAt()
        );
    }
}
