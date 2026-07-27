package com.weg.Maintenance_API.user.service;

import com.weg.Maintenance_API.audit.service.AuditService;
import com.weg.Maintenance_API.auth.service.ClientRequestMetadata;
import com.weg.Maintenance_API.enums.Role;
import com.weg.Maintenance_API.exception.type.ConflictException;
import com.weg.Maintenance_API.exception.type.InvalidRequestException;
import com.weg.Maintenance_API.exception.type.ResourceNotFoundException;
import com.weg.Maintenance_API.organization.dto.OrganizationSummaryResponse;
import com.weg.Maintenance_API.organization.entity.Organization;
import com.weg.Maintenance_API.organization.service.OrganizationService;
import com.weg.Maintenance_API.user.UserRepository;
import com.weg.Maintenance_API.user.dto.request.CreateUserRequest;
import com.weg.Maintenance_API.user.dto.response.UserCreationResponse;
import com.weg.Maintenance_API.user.entity.User;
import com.weg.Maintenance_API.user.event.UserCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserCreationService {

    private final UserRepository userRepository;
    private final OrganizationService organizationService;
    private final UserManagementPermissionService permissionService;
    private final UserIdentityPolicy userIdentityPolicy;
    private final UserAccountFactory userAccountFactory;
    private final TemporaryCredentialService temporaryCredentialService;
    private final AuditService auditService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public UserCreationResponse create(
            CreateUserRequest request,
            String actorEmail,
            ClientRequestMetadata metadata
    ) {
        User actor = userRepository.findByEmailIgnoreCase(actorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário autenticado"));
        Organization organization = resolveOrganization(actor, request.organizationId());

        try {
            permissionService.validateCanCreate(actor, request.role(), organization);
        } catch (AccessDeniedException exception) {
            auditService.record(
                    actor,
                    "UNAUTHORIZED_ROLE_CREATION_ATTEMPT",
                    "USER",
                    null,
                    metadata.endpoint(),
                    metadata.httpMethod(),
                    metadata.ipAddress(),
                    metadata.userAgent(),
                    false,
                    "Role solicitada: " + request.role()
            );
            throw exception;
        }

        if (!organization.isActive()) {
            throw new InvalidRequestException("A organização selecionada está inativa.");
        }

        String username = userIdentityPolicy.normalizeUsername(request.username());
        String email = userIdentityPolicy.normalizeEmail(request.email());
        userIdentityPolicy.validateName(request.name());
        userIdentityPolicy.validateUsername(username);
        userIdentityPolicy.validateEmail(email);
        userIdentityPolicy.validateAvailable(username, email);
        if (!organization.acceptsEmail(email)) {
            throw new InvalidRequestException(
                    "O domínio do e-mail não corresponde à organização selecionada."
            );
        }

        User user = userAccountFactory.create(
                request.name().trim(),
                username,
                email,
                "",
                request.role(),
                organization
        );
        String temporaryPassword = temporaryCredentialService.issue(user);

        try {
            userRepository.saveAndFlush(user);
        } catch (DataIntegrityViolationException exception) {
            throw new ConflictException(
                    "Não foi possível criar o usuário porque o e-mail ou username já está em uso."
            );
        }

        auditService.record(
                actor,
                "USER_CREATED",
                "USER",
                user.getId(),
                metadata.endpoint(),
                metadata.httpMethod(),
                metadata.ipAddress(),
                metadata.userAgent(),
                true,
                "Role criada: " + user.getRole()
                        + "; organização: " + organization.getName()
        );
        eventPublisher.publishEvent(new UserCreatedEvent(
                user.getId(),
                user.getName(),
                user.getEmail(),
                temporaryPassword
        ));
        return toResponse(user);
    }

    private Organization resolveOrganization(User actor, UUID requestedOrganizationId) {
        if (actor.getRole() == Role.COORDENADOR) {
            return actor.getOrganization();
        }
        if (requestedOrganizationId == null) {
            throw new InvalidRequestException(
                    "A organização é obrigatória para a criação feita por administrador."
            );
        }
        return organizationService.getRequired(requestedOrganizationId);
    }

    private UserCreationResponse toResponse(User user) {
        return new UserCreationResponse(
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
                false,
                "PENDING",
                user.getCreatedAt()
        );
    }
}
