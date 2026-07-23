package com.weg.Maintenance_API.user.service;

import com.weg.Maintenance_API.admin.entity.Admin;
import com.weg.Maintenance_API.audit.service.AuditService;
import com.weg.Maintenance_API.auth.service.ClientRequestMetadata;
import com.weg.Maintenance_API.coordinator.entity.Coordinator;
import com.weg.Maintenance_API.enums.Role;
import com.weg.Maintenance_API.exception.type.ConflictException;
import com.weg.Maintenance_API.exception.type.InvalidRequestException;
import com.weg.Maintenance_API.exception.type.ResourceNotFoundException;
import com.weg.Maintenance_API.organization.dto.OrganizationSummaryResponse;
import com.weg.Maintenance_API.organization.entity.Organization;
import com.weg.Maintenance_API.organization.service.OrganizationService;
import com.weg.Maintenance_API.student.entity.Student;
import com.weg.Maintenance_API.teacher.entity.Teacher;
import com.weg.Maintenance_API.user.UserRepository;
import com.weg.Maintenance_API.user.dto.request.CreateUserRequest;
import com.weg.Maintenance_API.user.dto.response.UserCreationResponse;
import com.weg.Maintenance_API.user.entity.User;
import com.weg.Maintenance_API.user.event.UserCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserCreationService {

    private static final Set<String> RESERVED_USERNAMES =
            Set.of("admin", "root", "system", "support", "administrator");

    private final UserRepository userRepository;
    private final OrganizationService organizationService;
    private final UserManagementPermissionService permissionService;
    private final TemporaryPasswordGenerator temporaryPasswordGenerator;
    private final PasswordEncoder passwordEncoder;
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

        String username = normalizeUsername(request.username());
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        validateUsername(username);
        validateUniqueness(username, email);
        if (!organization.acceptsEmail(email)) {
            throw new InvalidRequestException(
                    "O domínio do e-mail não corresponde à organização selecionada."
            );
        }

        String temporaryPassword = temporaryPasswordGenerator.generate();
        User user = createSubtype(
                request.role(),
                request.name().trim(),
                email,
                passwordEncoder.encode(temporaryPassword)
        );
        user.setUsername(username);
        user.setOrganization(organization);
        user.setPasswordChangeRequired(true);
        user.setTemporaryPasswordExpiresAt(LocalDateTime.now().plusDays(3));
        user.setEnabled(true);
        user.setAccountNonLocked(true);

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

    private User createSubtype(Role role, String name, String email, String passwordHash) {
        return switch (role) {
            case ADMIN -> new Admin(name, email, passwordHash);
            case COORDENADOR -> new Coordinator(name, email, passwordHash);
            case PROFESSOR -> new Teacher(name, email, passwordHash);
            case ALUNO -> new Student(
                    name,
                    email,
                    passwordHash,
                    UUID.randomUUID().toString()
            );
        };
    }

    private void validateUniqueness(String username, String email) {
        if (userRepository.existsByUsernameIgnoreCase(username)) {
            throw new ConflictException("O username informado já está cadastrado.");
        }
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ConflictException("O e-mail informado já está cadastrado.");
        }
    }

    private void validateUsername(String username) {
        if (RESERVED_USERNAMES.contains(username)) {
            throw new InvalidRequestException("O username informado é reservado.");
        }
        if (!username.matches("^[a-z0-9][a-z0-9._-]{2,49}$")) {
            throw new InvalidRequestException(
                    "O username deve começar com letra ou número e usar apenas letras minúsculas, números, ponto, hífen ou sublinhado."
            );
        }
    }

    private String normalizeUsername(String username) {
        String withoutAccents = Normalizer.normalize(username, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return withoutAccents
                .trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", ".");
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
