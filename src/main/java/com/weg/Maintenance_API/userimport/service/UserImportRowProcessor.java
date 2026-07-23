package com.weg.Maintenance_API.userimport.service;

import com.weg.Maintenance_API.audit.service.AuditService;
import com.weg.Maintenance_API.auth.service.ClientRequestMetadata;
import com.weg.Maintenance_API.enums.Role;
import com.weg.Maintenance_API.exception.type.ConflictException;
import com.weg.Maintenance_API.exception.type.InvalidRequestException;
import com.weg.Maintenance_API.organization.entity.Organization;
import com.weg.Maintenance_API.organization.repository.OrganizationRepository;
import com.weg.Maintenance_API.user.UserRepository;
import com.weg.Maintenance_API.user.entity.User;
import com.weg.Maintenance_API.user.event.UserCreatedEvent;
import com.weg.Maintenance_API.user.service.TemporaryCredentialService;
import com.weg.Maintenance_API.user.service.UserAccountFactory;
import com.weg.Maintenance_API.user.service.UserIdentityPolicy;
import com.weg.Maintenance_API.user.service.UserManagementPermissionService;
import com.weg.Maintenance_API.userimport.entity.UserImport;
import com.weg.Maintenance_API.userimport.entity.UserImportItem;
import com.weg.Maintenance_API.userimport.repository.UserImportItemRepository;
import com.weg.Maintenance_API.userimport.repository.UserImportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserImportRowProcessor {

    private final UserImportRepository userImportRepository;
    private final UserImportItemRepository itemRepository;
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final UserIdentityPolicy identityPolicy;
    private final UserManagementPermissionService permissionService;
    private final UserAccountFactory userAccountFactory;
    private final TemporaryCredentialService temporaryCredentialService;
    private final ApplicationEventPublisher eventPublisher;
    private final AuditService auditService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void process(
            UUID importId,
            UUID actorId,
            SpreadsheetUserRow row,
            boolean duplicateEmailInFile,
            boolean duplicateUsernameInFile,
            ClientRequestMetadata metadata
    ) {
        UserImport userImport = userImportRepository.findById(importId)
                .orElseThrow();
        User actor = userRepository.findById(actorId).orElseThrow();

        if (row.isEmpty()) {
            throw rowError("EMPTY_ROW", "row", "A linha está vazia.", null);
        }
        Role role = parseRole(row.role());
        if (duplicateEmailInFile) {
            throw rowError(
                    "DUPLICATE_EMAIL_IN_FILE",
                    "email",
                    "O e-mail está duplicado dentro da planilha.",
                    role
            );
        }
        if (duplicateUsernameInFile) {
            throw rowError(
                    "DUPLICATE_USERNAME_IN_FILE",
                    "username",
                    "O username está duplicado dentro da planilha.",
                    role
            );
        }

        String username = identityPolicy.normalizeUsername(row.username());
        String email = identityPolicy.normalizeEmail(row.email());
        validateIdentity(row, username, email, role);
        Organization organization = resolveOrganization(actor, row.organization(), role);
        validateOrganizationAndPermission(actor, organization, role, email);
        validateDatabaseDuplicates(username, email, role);

        User user = userAccountFactory.create(
                row.name().trim(),
                username,
                email,
                "",
                role,
                organization
        );
        String temporaryPassword = temporaryCredentialService.issue(user);
        userRepository.saveAndFlush(user);

        itemRepository.save(UserImportItem.success(
                userImport,
                row.rowNumber(),
                user.getName(),
                username,
                email,
                role,
                organization.getName(),
                user
        ));
        auditService.record(
                actor,
                "USER_IMPORTED",
                "USER",
                user.getId(),
                metadata.endpoint(),
                metadata.httpMethod(),
                metadata.ipAddress(),
                metadata.userAgent(),
                true,
                "Importação: " + importId + "; role: " + role
                        + "; organização: " + organization.getName()
        );
        eventPublisher.publishEvent(new UserCreatedEvent(
                user.getId(),
                user.getName(),
                user.getEmail(),
                temporaryPassword
        ));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordFailure(
            UUID importId,
            SpreadsheetUserRow row,
            UserImportRowException error
    ) {
        UserImport userImport = userImportRepository.findById(importId)
                .orElseThrow();
        itemRepository.save(UserImportItem.failure(
                userImport,
                row.rowNumber(),
                row.name(),
                identityPolicy.normalizeUsername(row.username()),
                identityPolicy.normalizeEmail(row.email()),
                error.role(),
                row.organization(),
                error.code(),
                error.field(),
                error.getMessage()
        ));
    }

    private void validateIdentity(
            SpreadsheetUserRow row,
            String username,
            String email,
            Role role
    ) {
        try {
            identityPolicy.validateName(row.name());
        } catch (InvalidRequestException exception) {
            throw rowError("INVALID_NAME", "name", exception.getMessage(), role);
        }
        try {
            identityPolicy.validateUsername(username);
        } catch (InvalidRequestException exception) {
            throw rowError("INVALID_USERNAME", "username", exception.getMessage(), role);
        }
        try {
            identityPolicy.validateEmail(email);
        } catch (InvalidRequestException exception) {
            throw rowError("INVALID_EMAIL", "email", exception.getMessage(), role);
        }
    }

    private void validateOrganizationAndPermission(
            User actor,
            Organization organization,
            Role role,
            String email
    ) {
        if (!organization.isActive()) {
            throw rowError(
                    "INACTIVE_ORGANIZATION",
                    "organization",
                    "A organização informada está inativa.",
                    role
            );
        }
        if (!organization.acceptsEmail(email)) {
            throw rowError(
                    "EMAIL_DOMAIN_MISMATCH",
                    "email",
                    "O domínio do e-mail não corresponde à organização.",
                    role
            );
        }
        try {
            permissionService.validateCanImport(actor, role, organization);
        } catch (AccessDeniedException exception) {
            String field = role == Role.ADMIN || role == Role.COORDENADOR
                    ? "role"
                    : "organization";
            throw rowError("ACCESS_DENIED", field, exception.getMessage(), role);
        }
    }

    private void validateDatabaseDuplicates(String username, String email, Role role) {
        userRepository.findByEmailIgnoreCase(email).ifPresent(existing -> {
            String message = existing.isEnabled()
                    ? "O e-mail já está cadastrado."
                    : "Existe um usuário inativo cadastrado com este e-mail.";
            throw rowError("DUPLICATE_EMAIL", "email", message, role);
        });
        if (userRepository.existsByUsernameIgnoreCase(username)) {
            throw rowError(
                    "DUPLICATE_USERNAME",
                    "username",
                    "O username já está cadastrado.",
                    role
            );
        }
    }

    private Organization resolveOrganization(User actor, String value, Role role) {
        if (actor.getRole() == Role.COORDENADOR) {
            Organization ownOrganization = actor.getOrganization();
            if (!value.isBlank() && !matchesOrganization(ownOrganization, value)) {
                throw rowError(
                        "CROSS_ORGANIZATION_IMPORT",
                        "organization",
                        "Coordenadores só podem importar para sua própria organização.",
                        role
                );
            }
            return ownOrganization;
        }
        if (value.isBlank()) {
            throw rowError(
                    "REQUIRED_ORGANIZATION",
                    "organization",
                    "A organização é obrigatória para importações realizadas por administrador.",
                    role
            );
        }
        try {
            UUID id = UUID.fromString(value.trim());
            return organizationRepository.findById(id)
                    .orElseThrow(() -> rowError(
                            "ORGANIZATION_NOT_FOUND",
                            "organization",
                            "A organização informada não existe.",
                            role
                    ));
        } catch (IllegalArgumentException ignored) {
            return organizationRepository.findByNameIgnoreCase(value.trim())
                    .or(() -> organizationRepository.findByEmailDomainIgnoreCase(value.trim()))
                    .orElseThrow(() -> rowError(
                            "ORGANIZATION_NOT_FOUND",
                            "organization",
                            "A organização informada não existe.",
                            role
                    ));
        }
    }

    private boolean matchesOrganization(Organization organization, String value) {
        String normalized = value.trim();
        return organization.getId().toString().equalsIgnoreCase(normalized)
                || organization.getName().equalsIgnoreCase(normalized)
                || organization.getEmailDomain().equalsIgnoreCase(
                        normalized.replaceFirst("^@", "")
                );
    }

    private Role parseRole(String value) {
        if (value == null || value.isBlank()) {
            throw rowError("REQUIRED_ROLE", "role", "A role é obrigatória.", null);
        }
        Role role = switch (value.trim().toUpperCase(Locale.ROOT)) {
            case "PROFESSOR", "TEACHER" -> Role.PROFESSOR;
            case "ALUNO", "STUDENT" -> Role.ALUNO;
            case "ADMIN" -> Role.ADMIN;
            case "COORDENADOR", "COORDINATOR" -> Role.COORDENADOR;
            default -> throw rowError("INVALID_ROLE", "role", "A role informada é inválida.", null);
        };
        if (role == Role.ADMIN || role == Role.COORDENADOR) {
            throw rowError(
                    "FORBIDDEN_IMPORT_ROLE",
                    "role",
                    "A importação permite apenas usuários PROFESSOR ou ALUNO.",
                    role
            );
        }
        return role;
    }

    private UserImportRowException rowError(
            String code,
            String field,
            String message,
            Role role
    ) {
        return new UserImportRowException(code, field, message, role);
    }
}
