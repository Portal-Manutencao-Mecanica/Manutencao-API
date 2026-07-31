package com.weg.Maintenance_API.user.service;

import com.weg.Maintenance_API.audit.service.AuditService;
import com.weg.Maintenance_API.auth.service.ClientRequestMetadata;
import com.weg.Maintenance_API.classgroup.entity.ClassGroup;
import com.weg.Maintenance_API.classgroup.repository.ClassGroupRepository;
import com.weg.Maintenance_API.enums.Role;
import com.weg.Maintenance_API.exception.type.ConflictException;
import com.weg.Maintenance_API.exception.type.InvalidRequestException;
import com.weg.Maintenance_API.exception.type.ResourceNotFoundException;
import com.weg.Maintenance_API.organization.dto.OrganizationSummaryResponse;
import com.weg.Maintenance_API.organization.entity.Organization;
import com.weg.Maintenance_API.organization.repository.OrganizationRepository;
import com.weg.Maintenance_API.student.entity.Student;
import com.weg.Maintenance_API.teacher.entity.Teacher;
import com.weg.Maintenance_API.user.UserRepository;
import com.weg.Maintenance_API.user.dto.request.CreateUserRequest;
import com.weg.Maintenance_API.user.dto.request.StudentDataRequest;
import com.weg.Maintenance_API.user.dto.request.TeacherDataRequest;
import com.weg.Maintenance_API.user.dto.response.UserCreationResponse;
import com.weg.Maintenance_API.user.entity.User;
import com.weg.Maintenance_API.user.event.UserCreatedEvent;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Validated
@RequiredArgsConstructor
public class UserCreationService {

    private final UserRepository userRepository;
    private final AuthenticatedUserService authenticatedUserService;
    private final OrganizationRepository organizationRepository;
    private final ClassGroupRepository classGroupRepository;
    private final UserManagementPermissionService permissionService;
    private final UserIdentityPolicy userIdentityPolicy;
    private final UserAccountFactory userAccountFactory;
    private final TemporaryCredentialService temporaryCredentialService;
    private final AuditService auditService;
    private final ApplicationEventPublisher eventPublisher;

    // Cria o usuario e seu perfil especifico na mesma transacao.
    @Transactional(rollbackFor = RuntimeException.class)
    public UserCreationResponse create(
            @Valid CreateUserRequest request,
            ClientRequestMetadata metadata
    ) {
        User actor = authenticatedUserService.requireCurrentUser();
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
            throw new InvalidRequestException("A organizacao selecionada esta inativa.");
        }

        String username = userIdentityPolicy.normalizeUsername(request.username());
        String email = userIdentityPolicy.normalizeEmail(request.email());
        userIdentityPolicy.validateName(request.name());
        userIdentityPolicy.validateUsername(username);
        userIdentityPolicy.validateEmail(email);
        userIdentityPolicy.validateAvailable(username, email);
        if (!organization.acceptsEmail(email)) {
            throw new InvalidRequestException(
                    "O dominio do e-mail nao corresponde a organizacao selecionada."
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
                    "Nao foi possivel criar o usuario porque o e-mail ou username ja esta em uso."
            );
        }

        configureSpecificProfile(user, request);

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
                        + "; organizacao: " + organization.getName()
        );
        eventPublisher.publishEvent(new UserCreatedEvent(
                user.getId(),
                user.getName(),
                user.getEmail(),
                temporaryPassword
        ));
        return toResponse(user);
    }

    // Configura dados do subtipo persistido pelo mapeamento JPA JOINED.
    private void configureSpecificProfile(User user, CreateUserRequest request) {
        switch (request.role()) {
            case ALUNO -> assignStudentToClassGroups(
                    requireStudent(user),
                    request.studentData()
            );
            case PROFESSOR -> assignTeacherToClassGroups(
                    requireTeacher(user),
                    request.teacherData()
            );
            case COORDENADOR, ADMIN -> {
                // Coordinator and admin do not have additional persisted profile data.
            }
        }
    }

    // Resolve turmas e atualiza o lado proprietario do relacionamento com aluno.
    private void assignStudentToClassGroups(
            Student student,
            StudentDataRequest profileData
    ) {
        List<ClassGroup> classGroups = resolveClassGroups(profileData.classGroupIds());
        student.setClassGroups(new ArrayList<>(classGroups));
        for (ClassGroup classGroup : classGroups) {
            if (!classGroup.getStudents().contains(student)) {
                classGroup.getStudents().add(student);
            }
        }
        classGroupRepository.saveAll(classGroups);
    }

    // Resolve turmas e atualiza o lado proprietario do relacionamento com professor.
    private void assignTeacherToClassGroups(
            Teacher teacher,
            TeacherDataRequest profileData
    ) {
        List<ClassGroup> classGroups = resolveClassGroups(profileData.classGroupIds());
        teacher.setClassGroups(new ArrayList<>(classGroups));
        for (ClassGroup classGroup : classGroups) {
            if (!classGroup.getTeachers().contains(teacher)) {
                classGroup.getTeachers().add(teacher);
            }
        }
        classGroupRepository.saveAll(classGroups);
    }

    // Resolve cada UUID de turma pelo repository, sem consultas em mappers.
    private List<ClassGroup> resolveClassGroups(List<UUID> classGroupIds) {
        if (classGroupIds == null || classGroupIds.isEmpty()) {
            return List.of();
        }

        Set<UUID> uniqueIds = new LinkedHashSet<>(classGroupIds);
        return uniqueIds.stream()
                .map(id -> classGroupRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Turma", id)))
                .toList();
    }

    // Garante que a factory criou o subtipo compativel com a role.
    private Student requireStudent(User user) {
        if (user instanceof Student student) {
            return student;
        }
        throw new IllegalStateException("O perfil de aluno nao foi criado para o usuario.");
    }

    // Garante que a factory criou o subtipo compativel com a role.
    private Teacher requireTeacher(User user) {
        if (user instanceof Teacher teacher) {
            return teacher;
        }
        throw new IllegalStateException("O perfil de professor nao foi criado para o usuario.");
    }

    // Resolve a organizacao pelo repository conforme a permissao do ator.
    private Organization resolveOrganization(User actor, UUID requestedOrganizationId) {
        if (actor.getRole() == Role.COORDENADOR) {
            return actor.getOrganization();
        }
        if (requestedOrganizationId == null) {
            throw new InvalidRequestException(
                    "A organizacao e obrigatoria para a criacao feita por administrador."
            );
        }
        return organizationRepository.findById(requestedOrganizationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Organizacao",
                        requestedOrganizationId
                ));
    }

    // Converte os dados para o formato necessario.
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
