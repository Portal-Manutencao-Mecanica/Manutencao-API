package com.weg.Maintenance_API.userimport.service;

import com.weg.Maintenance_API.audit.service.AuditService;
import com.weg.Maintenance_API.auth.service.ClientRequestMetadata;
import com.weg.Maintenance_API.enums.Role;
import com.weg.Maintenance_API.enums.OrganizationType;
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

    // Executa a operacao deste metodo.
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void process(
            UUID importId,
            UUID actorId,
            SpreadsheetUserRow row,
            boolean duplicateEmailInFile,
            ClientRequestMetadata metadata
    ) {
        UserImport userImport = userImportRepository.findById(importId)
                .orElseThrow();
        User actor = userRepository.findById(actorId).orElseThrow();

        if (row.isEmpty()) {
            throw rowError("EMPTY_ROW", "row", "A linha estÃ¡ vazia.", null);
        }
        Role role = parseRole(row.role());
        if (duplicateEmailInFile) {
            throw rowError(
                    "DUPLICATE_EMAIL_IN_FILE",
                    "email",
                    "O e-mail estÃ¡ duplicado dentro da planilha.",
                    role
            );
        }
        String email = identityPolicy.normalizeEmail(row.email());
        validateIdentity(row, email, role);
        Organization organization = resolveOrganization(actor, row.organization(), role);
        validateOrganizationAndPermission(actor, organization, role, email);
        validateEmailDuplicate(email, role);
        String username = identityPolicy.generateUsername(row.name());

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
                "ImportaÃ§Ã£o: " + importId + "; role: " + role
                        + "; organizaÃ§Ã£o: " + organization.getName()
        );
        eventPublisher.publishEvent(new UserCreatedEvent(
                user.getId(),
                user.getName(),
                user.getEmail(),
                temporaryPassword
        ));
    }

    // Executa o fluxo de comunicacao ou registro.
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
                "",
                identityPolicy.normalizeEmail(row.email()),
                error.role(),
                row.organization(),
                error.code(),
                error.field(),
                error.getMessage()
        ));
    }

    // Valida a regra aplicada por este metodo.
    private void validateIdentity(
            SpreadsheetUserRow row,
            String email,
            Role role
    ) {
        try {
            identityPolicy.validateName(row.name());
        } catch (InvalidRequestException exception) {
            throw rowError("INVALID_NAME", "name", exception.getMessage(), role);
        }
        try {
            identityPolicy.validateEmail(email);
        } catch (InvalidRequestException exception) {
            throw rowError("INVALID_EMAIL", "email", exception.getMessage(), role);
        }
    }

    // Valida a regra aplicada por este metodo.
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
                    "A organizaÃ§Ã£o informada estÃ¡ inativa.",
                    role
            );
        }
        if (!organization.acceptsEmail(email)) {
            throw rowError(
                    "EMAIL_DOMAIN_MISMATCH",
                    "email",
                    "O domÃ­nio do e-mail nÃ£o corresponde Ã  organizaÃ§Ã£o.",
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

    // Valida a regra aplicada por este metodo.
    private void validateEmailDuplicate(String email, Role role) {
        userRepository.findByEmailIgnoreCase(email).ifPresent(existing -> {
            String message = existing.isEnabled()
                    ? "O e-mail jÃ¡ estÃ¡ cadastrado."
                    : "Existe um usuÃ¡rio inativo cadastrado com este e-mail.";
            throw rowError("DUPLICATE_EMAIL", "email", message, role);
        });
    }

    // Executa a operacao deste metodo.
    private Organization resolveOrganization(User actor, String value, Role role) {
        if (value.isBlank()) {
            throw rowError(
                    "REQUIRED_ORGANIZATION",
                    "organization",
                    "A organização é obrigatória para a importação de usuários.",
                    role
            );
        }
        try {
            OrganizationType organizationType = parseOrganizationType(value, role);
            if (organizationRepository.findAllByType(organizationType).size() > 1) {
                throw rowError(
                        "AMBIGUOUS_ORGANIZATION_TYPE",
                        "organization",
                        "Há mais de uma organização cadastrada para o tipo " + organizationType + ".",
                        role
                );
            }
            return organizationRepository.findByType(organizationType)
                    .orElseThrow(() -> rowError(
                            "ORGANIZATION_NOT_FOUND",
                            "organization",
                            "A organizaÃ§Ã£o informada nÃ£o existe.",
                            role
                    ));
        } catch (IllegalArgumentException ignored) {
            return organizationRepository.findByNameIgnoreCase(value.trim())
                    .or(() -> organizationRepository.findByEmailDomainIgnoreCase(value.trim()))
                    .orElseThrow(() -> rowError(
                            "ORGANIZATION_NOT_FOUND",
                            "organization",
                            "A organizaÃ§Ã£o informada nÃ£o existe.",
                            role
                    ));
        }
    }

    // Valida a regra aplicada por este metodo.
    private boolean matchesOrganization(Organization organization, String value) {
        String normalized = value.trim();
        return organization.getId().toString().equalsIgnoreCase(normalized)
                || organization.getName().equalsIgnoreCase(normalized)
                || organization.getEmailDomain().equalsIgnoreCase(
                        normalized.replaceFirst("^@", "")
                );
    }

    private OrganizationType parseOrganizationType(String value, Role role) {
        try {
            return OrganizationType.valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw rowError(
                    "INVALID_ORGANIZATION",
                    "organization",
                    "Organização inválida. Valores permitidos: SENAI, WEG ou OTHER.",
                    role
            );
        }
    }

    // Executa a operacao deste metodo.
    private Role parseRole(String value) {
        if (value == null || value.isBlank()) {
            throw rowError("REQUIRED_ROLE", "role", "A role Ã© obrigatÃ³ria.", null);
        }
        Role role = switch (value.trim().toUpperCase(Locale.ROOT)) {
            case "PROFESSOR", "TEACHER" -> Role.PROFESSOR;
            case "ALUNO", "STUDENT" -> Role.ALUNO;
            case "ADMIN" -> Role.ADMIN;
            case "COORDENADOR", "COORDINATOR" -> Role.COORDENADOR;
            default -> throw rowError("INVALID_ROLE", "role", "A role informada Ã© invÃ¡lida.", null);
        };
        return role;
    }

    // Executa a operacao deste metodo.
    private UserImportRowException rowError(
            String code,
            String field,
            String message,
            Role role
    ) {
        return new UserImportRowException(code, field, message, role);
    }
}
