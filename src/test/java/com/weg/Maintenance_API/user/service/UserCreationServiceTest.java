package com.weg.Maintenance_API.user.service;

import com.weg.Maintenance_API.admin.entity.Admin;
import com.weg.Maintenance_API.audit.service.AuditService;
import com.weg.Maintenance_API.auth.service.ClientRequestMetadata;
import com.weg.Maintenance_API.classgroup.entity.ClassGroup;
import com.weg.Maintenance_API.classgroup.repository.ClassGroupRepository;
import com.weg.Maintenance_API.coordinator.entity.Coordinator;
import com.weg.Maintenance_API.enums.OrganizationType;
import com.weg.Maintenance_API.enums.Role;
import com.weg.Maintenance_API.organization.entity.Organization;
import com.weg.Maintenance_API.organization.repository.OrganizationRepository;
import com.weg.Maintenance_API.student.entity.Student;
import com.weg.Maintenance_API.teacher.entity.Teacher;
import com.weg.Maintenance_API.user.UserRepository;
import com.weg.Maintenance_API.user.dto.request.CreateUserRequest;
import com.weg.Maintenance_API.user.dto.request.StudentDataRequest;
import com.weg.Maintenance_API.user.dto.request.TeacherDataRequest;
import com.weg.Maintenance_API.user.entity.User;
import com.weg.Maintenance_API.user.event.UserCreatedEvent;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
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
import java.util.List;
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
    private AuthenticatedUserService authenticatedUserService;
    @Mock
    private OrganizationRepository organizationRepository;
    @Mock
    private ClassGroupRepository classGroupRepository;
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
    private Validator validator;

    @BeforeEach
    void setUp() {
        organization = new Organization("Local", OrganizationType.OTHER, "local.com");
        organization.setId(UUID.randomUUID());
        validator = Validation.buildDefaultValidatorFactory().getValidator();

        userCreationService = new UserCreationService(
                userRepository,
                authenticatedUserService,
                organizationRepository,
                classGroupRepository,
                new UserManagementPermissionService(),
                new UserIdentityPolicy(userRepository),
                new UserAccountFactory(),
                new TemporaryCredentialService(
                        temporaryPasswordGenerator,
                        passwordEncoder
                ),
                auditService,
                eventPublisher
        );
    }

    @Test
    void adminCreatesStudentProfileWithClassGroupsAndCredentialEvent() {
        Admin actor = adminActor();
        ClassGroup classGroup = new ClassGroup();
        classGroup.setId(UUID.randomUUID());

        when(authenticatedUserService.requireCurrentUser()).thenReturn(actor);
        when(organizationRepository.findById(organization.getId()))
                .thenReturn(Optional.of(organization));
        when(classGroupRepository.findById(classGroup.getId()))
                .thenReturn(Optional.of(classGroup));
        when(temporaryPasswordGenerator.generate()).thenReturn("Temp@1234Ab");
        when(passwordEncoder.encode("Temp@1234Ab")).thenReturn("bcrypt-hash");
        when(userRepository.existsByUsernameIgnoreCase("aluno.teste1")).thenReturn(false);
        when(userRepository.existsByEmailIgnoreCase("aluno@local.com")).thenReturn(false);
        saveUserWithGeneratedValues();

        userCreationService.create(
                new CreateUserRequest(
                        "Aluno Teste",
                        null,
                        "aluno@local.com",
                        "CARD-ALUNO",
                        Role.ALUNO,
                        organization.getId(),
                        new StudentDataRequest(List.of(classGroup.getId())),
                        null
                ),
                metadata()
        );

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).saveAndFlush(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertTrue(savedUser instanceof Student);
        assertEquals("aluno.teste1", savedUser.getUsername());
        assertEquals("bcrypt-hash", savedUser.getPassword());
        assertNotEquals("Temp@1234Ab", savedUser.getPassword());
        assertTrue(savedUser.isPasswordChangeRequired());
        assertEquals(List.of(classGroup), ((Student) savedUser).getClassGroups());
        assertEquals(List.of(savedUser), classGroup.getStudents());
        verify(classGroupRepository).saveAll(List.of(classGroup));
        verify(eventPublisher).publishEvent(any(UserCreatedEvent.class));
    }

    @Test
    void adminCreatesTeacherAndCoordinatorProfilesWithoutCoordinatorData() {
        Admin actor = adminActor();
        ClassGroup classGroup = new ClassGroup();
        classGroup.setId(UUID.randomUUID());
        configureActorAndIdentityMocks(actor);
        when(classGroupRepository.findById(classGroup.getId()))
                .thenReturn(Optional.of(classGroup));
        saveUserWithGeneratedValues();

        userCreationService.create(
                new CreateUserRequest(
                        "Professor Teste",
                        "professor.teste",
                        "professor@local.com",
                        "CARD-PROFESSOR",
                        Role.PROFESSOR,
                        organization.getId(),
                        null,
                        new TeacherDataRequest(List.of(classGroup.getId()))
                ),
                metadata()
        );
        userCreationService.create(
                new CreateUserRequest(
                        "Coordenador Teste",
                        "coordenador.teste",
                        "coordenador@local.com",
                        "CARD-COORDENADOR",
                        Role.COORDENADOR,
                        organization.getId(),
                        null,
                        null
                ),
                metadata()
        );

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, org.mockito.Mockito.times(2)).saveAndFlush(userCaptor.capture());
        assertTrue(userCaptor.getAllValues().get(0) instanceof Teacher);
        assertTrue(userCaptor.getAllValues().get(1) instanceof Coordinator);
        assertEquals(List.of(userCaptor.getAllValues().get(0)), classGroup.getTeachers());
    }

    @Test
    void profileDataValidationRequiresTheMatchingRoleAndRejectsOtherData() {
        assertTrue(validator.validate(request(Role.ALUNO, null, null)).stream()
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("studentData")));
        assertTrue(validator.validate(request(
                Role.ALUNO,
                new StudentDataRequest(List.of()),
                null
        )).stream().anyMatch(violation -> violation.getPropertyPath().toString()
                .equals("studentData.classGroupIds")));
        assertTrue(validator.validate(request(
                Role.PROFESSOR,
                null,
                new TeacherDataRequest(List.of())
        )).stream().anyMatch(violation -> violation.getPropertyPath().toString()
                .equals("teacherData.classGroupIds")));
        assertTrue(validator.validate(request(
                Role.PROFESSOR,
                new StudentDataRequest(List.of(UUID.randomUUID())),
                new TeacherDataRequest(List.of(UUID.randomUUID()))
        )).stream().anyMatch(violation -> violation.getPropertyPath().toString().equals("teacherData")));
        assertTrue(validator.validate(request(
                Role.COORDENADOR,
                null,
                null
        )).isEmpty());
        assertTrue(validator.validate(request(
                Role.ADMIN,
                null,
                new TeacherDataRequest(List.of(UUID.randomUUID()))
        )).stream().anyMatch(violation -> violation.getPropertyPath().toString().equals("role")));
    }

    @Test
    void coordinatorCannotCreateAdministrator() {
        Coordinator actor = new Coordinator("Coordenador", "coord@local.com", "hash");
        actor.setId(UUID.randomUUID());
        actor.setOrganization(organization);
        when(authenticatedUserService.requireCurrentUser()).thenReturn(actor);

        assertThrows(AccessDeniedException.class, () ->
                userCreationService.create(
                        new CreateUserRequest(
                                "Admin Indevido",
                                "admin.indevido",
                                "admin.indevido@local.com",
                                "CARD-ADMIN",
                                Role.ADMIN,
                                organization.getId(),
                                null,
                                null
                        ),
                        metadata()
                ));
    }

    @Test
    void generatesSequentialUsernamesUsingTheCompleteName() {
        when(userRepository.existsByUsernameIgnoreCase("junior.da.silva1")).thenReturn(true);
        when(userRepository.existsByUsernameIgnoreCase("junior.da.silva2")).thenReturn(false);
        when(userRepository.existsByUsernameIgnoreCase("junior.souza1")).thenReturn(false);

        UserIdentityPolicy identityPolicy = new UserIdentityPolicy(userRepository);

        assertEquals("junior.da.silva2", identityPolicy.generateUsername("Junior da Silva"));
        assertEquals("junior.souza1", identityPolicy.generateUsername("Junior Souza"));
    }

    private Admin adminActor() {
        Admin actor = new Admin("Admin", "admin@local.com", "actor-hash");
        actor.setId(UUID.randomUUID());
        actor.setOrganization(organization);
        return actor;
    }

    private void configureActorAndIdentityMocks(Admin actor) {
        when(authenticatedUserService.requireCurrentUser()).thenReturn(actor);
        when(organizationRepository.findById(organization.getId()))
                .thenReturn(Optional.of(organization));
        when(temporaryPasswordGenerator.generate()).thenReturn("Temp@1234Ab");
        when(passwordEncoder.encode("Temp@1234Ab")).thenReturn("bcrypt-hash");
        when(userRepository.existsByUsernameIgnoreCase(any(String.class))).thenReturn(false);
        when(userRepository.existsByEmailIgnoreCase(any(String.class))).thenReturn(false);
    }

    private void saveUserWithGeneratedValues() {
        when(userRepository.saveAndFlush(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setId(UUID.randomUUID());
            saved.setCreatedAt(LocalDateTime.now());
            saved.setUpdatedAt(LocalDateTime.now());
            return saved;
        });
    }

    private CreateUserRequest request(
            Role role,
            StudentDataRequest studentData,
            TeacherDataRequest teacherData
    ) {
        return new CreateUserRequest(
                "Usuario Teste",
                "usuario.teste",
                "usuario@local.com",
                "CARD-USUARIO",
                role,
                organization.getId(),
                studentData,
                teacherData
        );
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
