package com.weg.Maintenance_API.user.service;

import com.weg.Maintenance_API.admin.entity.Admin;
import com.weg.Maintenance_API.audit.service.AuditService;
import com.weg.Maintenance_API.auth.service.ClientRequestMetadata;
import com.weg.Maintenance_API.coordinator.entity.Coordinator;
import com.weg.Maintenance_API.enums.Role;
import com.weg.Maintenance_API.organization.entity.Organization;
import com.weg.Maintenance_API.organization.entity.OrganizationType;
import com.weg.Maintenance_API.organization.service.OrganizationService;
import com.weg.Maintenance_API.user.UserRepository;
import com.weg.Maintenance_API.user.dto.request.CreateUserRequest;
import com.weg.Maintenance_API.user.entity.User;
import com.weg.Maintenance_API.user.event.UserCreatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserCreationServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private OrganizationService organizationService;
    @Mock
    private TemporaryPasswordGenerator temporaryPasswordGenerator;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuditService auditService;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    private UserCreationService userCreationService;
    private Organization organization;

    @BeforeEach
    void setUp() {
        organization = new Organization("Local", OrganizationType.OTHER, "local.com");
        organization.setId(UUID.randomUUID());

        userCreationService = new UserCreationService(
                userRepository,
                organizationService,
                new UserManagementPermissionService(),
                temporaryPasswordGenerator,
                passwordEncoder,
                auditService,
                eventPublisher
        );
    }

    @Test
    void adminCreatesStudentWithHashedTemporaryPasswordAndEmailEvent() {
        Admin actor = new Admin("Admin", "admin@local.com", "actor-hash");
        actor.setId(UUID.randomUUID());
        actor.setOrganization(organization);

        when(userRepository.findByEmailIgnoreCase(actor.getEmail()))
                .thenReturn(Optional.of(actor));
        when(organizationService.getRequired(organization.getId()))
                .thenReturn(organization);
        when(temporaryPasswordGenerator.generate()).thenReturn("Temp@1234Ab");
        when(passwordEncoder.encode("Temp@1234Ab")).thenReturn("bcrypt-hash");
        when(userRepository.saveAndFlush(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setId(UUID.randomUUID());
            saved.setCreatedAt(LocalDateTime.now());
            saved.setUpdatedAt(LocalDateTime.now());
            return saved;
        });

        userCreationService.create(
                new CreateUserRequest(
                        "Aluno Teste",
                        "Aluno Teste",
                        "aluno@local.com",
                        Role.ALUNO,
                        organization.getId()
                ),
                actor.getEmail(),
                metadata()
        );

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).saveAndFlush(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertEquals("aluno.teste", savedUser.getUsername());
        assertEquals("bcrypt-hash", savedUser.getPassword());
        assertNotEquals("Temp@1234Ab", savedUser.getPassword());
        assertTrue(savedUser.isPasswordChangeRequired());
        verify(eventPublisher).publishEvent(any(UserCreatedEvent.class));
    }

    @Test
    void coordinatorCannotCreateAdministrator() {
        Coordinator actor =
                new Coordinator("Coordenador", "coord@local.com", "hash");
        actor.setId(UUID.randomUUID());
        actor.setOrganization(organization);
        when(userRepository.findByEmailIgnoreCase(actor.getEmail()))
                .thenReturn(Optional.of(actor));

        assertThrows(AccessDeniedException.class, () ->
                userCreationService.create(
                        new CreateUserRequest(
                                "Admin Indevido",
                                "admin.indevido",
                                "admin.indevido@local.com",
                                Role.ADMIN,
                                organization.getId()
                        ),
                        actor.getEmail(),
                        metadata()
                ));
    }

    private ClientRequestMetadata metadata() {
        return new ClientRequestMetadata(
                "/users",
                "POST",
                "127.0.0.1",
                "JUnit"
        );
    }
}
