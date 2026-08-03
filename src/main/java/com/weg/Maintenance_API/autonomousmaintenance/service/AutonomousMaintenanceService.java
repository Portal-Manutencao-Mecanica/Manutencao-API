package com.weg.Maintenance_API.autonomousmaintenance.service;

import com.weg.Maintenance_API.autonomousmaintenance.dto.requests.AutonomousMaintenanceApprovalRequest;
import com.weg.Maintenance_API.autonomousmaintenance.dto.requests.AutonomousMaintenanceDtoRequest;
import com.weg.Maintenance_API.autonomousmaintenance.dto.response.AutonomousMaintenanceDtoResponse;
import com.weg.Maintenance_API.autonomousmaintenance.entity.AutonomousMaintenance;
import com.weg.Maintenance_API.autonomousmaintenance.mapper.AutonomousMaintenanceMapper;
import com.weg.Maintenance_API.autonomousmaintenance.repository.AutonomousMaintenanceRepository;
import com.weg.Maintenance_API.classgroup.entity.ClassGroup;
import com.weg.Maintenance_API.coordinator.repository.CoordinatorRepository;
import com.weg.Maintenance_API.enums.AutonomousMaintenanceStatus;
import com.weg.Maintenance_API.enums.EquipmentCondition;
import com.weg.Maintenance_API.enums.MaintenanceType;
import com.weg.Maintenance_API.enums.Role;
import com.weg.Maintenance_API.enums.TaskCriticality;
import com.weg.Maintenance_API.enums.TaskSituation;
import com.weg.Maintenance_API.event.entity.Event;
import com.weg.Maintenance_API.event.repository.EventRepository;
import com.weg.Maintenance_API.exception.type.InvalidRequestException;
import com.weg.Maintenance_API.exception.type.InvalidStateException;
import com.weg.Maintenance_API.exception.type.ResourceNotFoundException;
import com.weg.Maintenance_API.machine.entity.Machine;
import com.weg.Maintenance_API.machine.repository.MachineRepository;
import com.weg.Maintenance_API.notification.service.NotificationService;
import com.weg.Maintenance_API.student.entity.Student;
import com.weg.Maintenance_API.student.repository.StudentRepository;
import com.weg.Maintenance_API.teacher.entity.Teacher;
import com.weg.Maintenance_API.user.entity.User;
import com.weg.Maintenance_API.user.service.AuthenticatedUserService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AutonomousMaintenanceService {

    private static final String ABOUT = "MANUTENCAO_AUTONOMA";

    private final AutonomousMaintenanceRepository repository;
    private final AutonomousMaintenanceMapper mapper;
    private final MachineRepository machineRepository;
    private final StudentRepository studentRepository;
    private final CoordinatorRepository coordinatorRepository;
    private final EventRepository eventRepository;
    private final NotificationService notificationService;
    private final AuthenticatedUserService authenticatedUserService;

    @Transactional
    public AutonomousMaintenanceDtoResponse create(AutonomousMaintenanceDtoRequest request) {
        User currentUser = authenticatedUserService.requireCurrentUser();
        if (!(currentUser instanceof Teacher teacher) || currentUser.getRole() != Role.PROFESSOR) {
            throw new AccessDeniedException("Somente professores podem criar manutencoes autonomas.");
        }

        AutonomousMaintenance maintenance = mapper.toEntity(request);
        maintenance.setInspectedMachine(findMachine(request.inspectedMachineId()));
        maintenance.setResponsibleTeacher(teacher);
        maintenance.setCreatedBy(currentUser);
        maintenance.setAssignedStudents(validateStudents(request.studentIds(), teacher));
        maintenance.setStatus(AutonomousMaintenanceStatus.PENDENTE_APROVACAO_COORDENADOR);

        AutonomousMaintenance saved = repository.save(maintenance);
        notifyCoordinators(
                saved,
                "Nova manutencao autonoma pendente",
                "A manutencao autonoma da maquina " + saved.getInspectedMachine().getName()
                        + " aguarda aprovacao."
        );
        return mapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<AutonomousMaintenanceDtoResponse> getAll(
            AutonomousMaintenanceStatus status,
            Pageable pageable
    ) {
        User currentUser = authenticatedUserService.requireCurrentUser();
        return repository.findAll(visibilitySpecification(currentUser, status), pageable)
                .map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public AutonomousMaintenanceDtoResponse getById(UUID id) {
        User currentUser = authenticatedUserService.requireCurrentUser();
        AutonomousMaintenance maintenance = findById(id);
        ensureCanView(maintenance, currentUser);
        return mapper.toResponse(maintenance);
    }

    @Transactional
    public AutonomousMaintenanceDtoResponse update(
            UUID id,
            AutonomousMaintenanceDtoRequest request
    ) {
        User currentUser = authenticatedUserService.requireCurrentUser();
        AutonomousMaintenance maintenance = findByIdForUpdate(id);
        ensurePending(maintenance);
        ensureCanChange(maintenance, currentUser);

        maintenance.setEquipmentSituation(request.equipmentSituation());
        maintenance.setScheduledFor(request.scheduledFor());
        maintenance.setInspectedAt(request.inspectedAt());
        maintenance.setInspectedMachine(findMachine(request.inspectedMachineId()));
        maintenance.setEquipmentCondition(request.equipmentCondition());
        maintenance.setIdentifiedNonconformities(normalizeOptional(request.identifiedNonconformities()));
        maintenance.setAssignedStudents(validateStudents(
                request.studentIds(), maintenance.getResponsibleTeacher()));

        notifyCoordinators(
                maintenance,
                "Manutencao autonoma alterada",
                "A manutencao autonoma " + maintenance.getId()
                        + " foi alterada e continua aguardando aprovacao."
        );
        return mapper.toResponse(repository.save(maintenance));
    }

    @Transactional
    public void delete(UUID id) {
        User currentUser = authenticatedUserService.requireCurrentUser();
        AutonomousMaintenance maintenance = findByIdForUpdate(id);
        ensurePending(maintenance);
        ensureCanChange(maintenance, currentUser);

        notifyCoordinators(
                maintenance,
                "Manutencao autonoma cancelada",
                "A manutencao autonoma " + maintenance.getId() + " foi cancelada."
        );
        repository.delete(maintenance);
    }

    @Transactional
    public AutonomousMaintenanceDtoResponse decide(
            UUID id,
            AutonomousMaintenanceApprovalRequest request
    ) {
        User coordinator = authenticatedUserService.requireCurrentUser();
        if (coordinator.getRole() != Role.COORDENADOR) {
            throw new AccessDeniedException("Somente coordenadores podem decidir manutencoes autonomas.");
        }
        if (!request.approved() && normalizeOptional(request.reason()) == null) {
            throw new InvalidRequestException("O motivo da reprovacao e obrigatorio.");
        }

        AutonomousMaintenance maintenance = findByIdForUpdate(id);
        ensureSameOrganization(maintenance.getCreatedBy(), coordinator);
        ensurePending(maintenance);

        LocalDateTime decidedAt = LocalDateTime.now();
        maintenance.setCoordinatorApprover(coordinator);
        maintenance.setApprovedAt(decidedAt);
        maintenance.setRejectionReason(request.approved() ? null : request.reason().trim());

        if (request.approved()) {
            maintenance.setStatus(AutonomousMaintenanceStatus.APROVADA_PELO_COORDENADOR);
            maintenance.setCalendarEvent(createCalendarEvent(maintenance, decidedAt));
            notifyApproved(maintenance);
        } else {
            maintenance.setStatus(AutonomousMaintenanceStatus.REPROVADA_PELO_COORDENADOR);
            notificationService.notifyUser(
                    maintenance.getCreatedBy(),
                    "Manutencao autonoma reprovada",
                    ABOUT,
                    "A manutencao autonoma da maquina "
                            + maintenance.getInspectedMachine().getName()
                            + " foi reprovada. Motivo: " + maintenance.getRejectionReason()
            );
        }

        return mapper.toResponse(repository.save(maintenance));
    }

    private Specification<AutonomousMaintenance> visibilitySpecification(
            User user,
            AutonomousMaintenanceStatus status
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            switch (user.getRole()) {
                case PROFESSOR -> predicates.add(
                        criteriaBuilder.equal(root.get("createdBy").get("id"), user.getId()));
                case COORDENADOR -> predicates.add(criteriaBuilder.equal(
                        root.get("createdBy").get("organization").get("id"),
                        user.getOrganization().getId()));
                case ALUNO -> {
                    query.distinct(true);
                    predicates.add(criteriaBuilder.equal(
                            root.join("assignedStudents").get("id"), user.getId()));
                    predicates.add(criteriaBuilder.equal(
                            root.get("status"),
                            AutonomousMaintenanceStatus.APROVADA_PELO_COORDENADOR));
                }
                case ADMIN -> {
                    // Acesso administrativo global conforme o padrao atual do projeto.
                }
                default -> predicates.add(criteriaBuilder.disjunction());
            }
            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }

    private void ensureCanView(AutonomousMaintenance maintenance, User user) {
        boolean allowed = switch (user.getRole()) {
            case ADMIN -> true;
            case PROFESSOR -> maintenance.getCreatedBy().getId().equals(user.getId());
            case COORDENADOR -> sameOrganization(maintenance.getCreatedBy(), user);
            case ALUNO -> maintenance.getStatus()
                    == AutonomousMaintenanceStatus.APROVADA_PELO_COORDENADOR
                    && maintenance.getAssignedStudents().stream()
                    .anyMatch(student -> student.getId().equals(user.getId()));
        };
        if (!allowed) {
            throw new AccessDeniedException("A manutencao autonoma nao esta disponivel para este usuario.");
        }
    }

    private void ensureCanChange(AutonomousMaintenance maintenance, User user) {
        boolean allowed = user.getRole() == Role.ADMIN
                || (user.getRole() == Role.PROFESSOR
                && maintenance.getCreatedBy().getId().equals(user.getId()));
        if (!allowed) {
            throw new AccessDeniedException("Somente o professor criador pode alterar esta manutencao.");
        }
    }

    private void ensurePending(AutonomousMaintenance maintenance) {
        if (maintenance.getStatus()
                != AutonomousMaintenanceStatus.PENDENTE_APROVACAO_COORDENADOR) {
            throw new InvalidStateException("A manutencao autonoma ja recebeu uma decisao.");
        }
    }

    private void ensureSameOrganization(User maintenanceOwner, User coordinator) {
        if (!sameOrganization(maintenanceOwner, coordinator)) {
            throw new AccessDeniedException(
                    "O coordenador nao pertence a organizacao da manutencao autonoma.");
        }
    }

    private boolean sameOrganization(User first, User second) {
        return first.getOrganization() != null
                && second.getOrganization() != null
                && first.getOrganization().getId().equals(second.getOrganization().getId());
    }

    private List<Student> validateStudents(List<UUID> requestedIds, Teacher teacher) {
        Set<UUID> uniqueIds = new LinkedHashSet<>(requestedIds);
        if (uniqueIds.isEmpty()) {
            throw new InvalidRequestException("Informe pelo menos um aluno.");
        }

        Map<UUID, Student> studentsById = studentRepository.findAllById(uniqueIds).stream()
                .collect(Collectors.toMap(Student::getId, Function.identity()));
        if (studentsById.size() != uniqueIds.size()) {
            throw new InvalidRequestException("Um ou mais alunos nao foram encontrados.");
        }

        List<Student> students = uniqueIds.stream().map(studentsById::get).toList();
        for (Student student : students) {
            if (!student.isEnabled() || !student.isAccountNonLocked()) {
                throw new InvalidRequestException("Todos os alunos devem estar ativos.");
            }
            if (!sameOrganization(teacher, student)) {
                throw new InvalidRequestException(
                        "Todos os alunos devem pertencer a organizacao do professor.");
            }
        }

        validateTeacherClasses(teacher, students);
        return students;
    }

    private void validateTeacherClasses(Teacher teacher, List<Student> students) {
        if (teacher.getClassGroups() == null) {
            return;
        }
        List<ClassGroup> activeGroups = teacher.getClassGroups().stream()
                .filter(ClassGroup::isEnabled)
                .toList();
        if (activeGroups.isEmpty()) {
            return;
        }

        Set<UUID> allowedStudentIds = activeGroups.stream()
                .flatMap(group -> group.getStudents().stream())
                .map(Student::getId)
                .collect(Collectors.toSet());
        if (students.stream().anyMatch(student -> !allowedStudentIds.contains(student.getId()))) {
            throw new InvalidRequestException(
                    "Todos os alunos devem pertencer a uma turma ativa vinculada ao professor.");
        }
    }

    private Event createCalendarEvent(
            AutonomousMaintenance maintenance,
            LocalDateTime requestedAt
    ) {
        if (maintenance.getCalendarEvent() != null) {
            throw new InvalidStateException("A manutencao autonoma ja possui evento no calendario.");
        }
        Event event = new Event();
        event.setScheduledAction(
                "Manutencao autonoma - " + maintenance.getInspectedMachine().getName());
        event.setCriticality(maintenance.getEquipmentCondition() == EquipmentCondition.NAO_CONFORME
                ? TaskCriticality.ALTA
                : TaskCriticality.MEDIA);
        event.setScheduledFor(maintenance.getScheduledFor());
        event.setRequestedAt(requestedAt);
        event.setTeacher(maintenance.getResponsibleTeacher());
        event.setEquipment(null);
        event.setMachine(maintenance.getInspectedMachine());
        event.setPlace(maintenance.getInspectedMachine().getPlace());
        event.setMaintenanceType(MaintenanceType.AUTONOMA);
        event.setStatus(TaskSituation.PENDENTE);
        return eventRepository.save(event);
    }

    private void notifyApproved(AutonomousMaintenance maintenance) {
        String description = "A manutencao autonoma da maquina "
                + maintenance.getInspectedMachine().getName()
                + " foi aprovada e adicionada ao calendario para "
                + maintenance.getScheduledFor() + ".";
        notificationService.notifyUser(
                maintenance.getCreatedBy(),
                "Manutencao autonoma aprovada",
                ABOUT,
                description
        );
        maintenance.getAssignedStudents().forEach(student -> notificationService.notifyUser(
                student,
                "Nova manutencao autonoma atribuida",
                ABOUT,
                description
        ));
    }

    private void notifyCoordinators(
            AutonomousMaintenance maintenance,
            String title,
            String description
    ) {
        UUID organizationId = maintenance.getCreatedBy().getOrganization().getId();
        coordinatorRepository
                .findAllByOrganizationIdAndEnabledTrueAndAccountNonLockedTrue(organizationId)
                .forEach(coordinator -> notificationService.notifyUser(
                        coordinator, title, ABOUT, description));
    }

    private Machine findMachine(UUID id) {
        return machineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maquina", id));
    }

    private AutonomousMaintenance findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Manutencao autonoma", id));
    }

    private AutonomousMaintenance findByIdForUpdate(UUID id) {
        return repository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("Manutencao autonoma", id));
    }

    private String normalizeOptional(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
