package com.weg.Maintenance_API.maintenancerequest.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.weg.Maintenance_API.audit.service.AuditService;
import com.weg.Maintenance_API.auth.service.ClientRequestMetadata;
import com.weg.Maintenance_API.enums.MaintenanceRequestStatus;
import com.weg.Maintenance_API.enums.MediaType;
import com.weg.Maintenance_API.enums.MaintenanceType;
import com.weg.Maintenance_API.enums.TaskCriticality;
import com.weg.Maintenance_API.enums.TaskSituation;
import com.weg.Maintenance_API.enums.Priority;
import com.weg.Maintenance_API.enums.Role;
import com.weg.Maintenance_API.enums.Sector;
import com.weg.Maintenance_API.exception.type.InvalidStateException;
import com.weg.Maintenance_API.exception.type.ResourceNotFoundException;
import com.weg.Maintenance_API.maintenancerequest.dto.request.MaintenanceApprovalRequest;
import com.weg.Maintenance_API.maintenancerequest.dto.request.MaintenanceRequestPatchRequest;
import com.weg.Maintenance_API.maintenancerequest.dto.request.MaintenanceRequestRequest;
import com.weg.Maintenance_API.maintenancerequest.dto.response.MaintenanceRequestResponse;
import com.weg.Maintenance_API.maintenancerequest.entity.MaintenanceRequest;
import com.weg.Maintenance_API.maintenancerequest.mapper.MaintenanceRequestMapper;
import com.weg.Maintenance_API.maintenancerequest.repository.MaintenanceRepository;
import com.weg.Maintenance_API.machinelog.entity.MachineLog;
import com.weg.Maintenance_API.machinelog.repository.MachineLogRepository;
import com.weg.Maintenance_API.media.service.ImageMediaFactory;
import com.weg.Maintenance_API.notification.service.NotificationService;
import com.weg.Maintenance_API.service.EntityReferenceService;
import com.weg.Maintenance_API.student.entity.Student;
import com.weg.Maintenance_API.teacher.entity.Teacher;
import com.weg.Maintenance_API.user.UserRepository;
import com.weg.Maintenance_API.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

@Service
@RequiredArgsConstructor
public class MaintenanceRequestService {

    private final MaintenanceRepository maintenanceRepository;
    private final MachineLogRepository machineLogRepository;
    private final MaintenanceRequestMapper maintenanceRequestMapper;
    private final EntityReferenceService references;
    private final UserRepository userRepository;
    private final AuditService auditService;
    private final NotificationService notificationService;
    private final ImageMediaFactory imageMediaFactory;

    @Transactional
    public MaintenanceRequestResponse save(
            MaintenanceRequestRequest request,
            String authenticatedEmail
    ) {
        User creator = authenticatedUser(authenticatedEmail);
        MaintenanceRequest maintenanceRequest = maintenanceRequestMapper.toEntity(request);
        maintenanceRequest.setCreatedBy(creator);
        maintenanceRequest.setAssignedStudents(
                creator instanceof Student student ? List.of(student) : List.of()
        );
        maintenanceRequest.setPlace(references.place(request.placeId()));
        maintenanceRequest.setNotifiedTeacher(references.teacher(request.notifiedTeacherId()));
        maintenanceRequest.setMachine(references.machine(request.machineId()));
        maintenanceRequest.setMedia(imageMediaFactory.fromDataUrls(
                request.images(), creator, MediaType.MAINTENANCE_REQUEST,
                "ocorrencia", "Evidência da ocorrência", true
        ));
        maintenanceRequest.setStatus(MaintenanceRequestStatus.PENDENTE_APROVACAO_PROFESSOR);

        maintenanceRequest = maintenanceRepository.save(maintenanceRequest);
        registerMachineLog(maintenanceRequest, creator);
        notificationService.notifyUser(
                maintenanceRequest.getNotifiedTeacher(),
                "Nova solicitação de manutenção",
                "Solicitação aguardando aprovação",
                "Uma solicitação de manutenção foi enviada por " + creator.getName() + "."
        );
        return maintenanceRequestMapper.toResponse(maintenanceRequest);
    }

    private void registerMachineLog(MaintenanceRequest maintenanceRequest, User creator) {
        MachineLog machineLog = new MachineLog();
        machineLog.setTitle("Ocorrência registrada");
        machineLog.setDescription(maintenanceRequest.getDescription());
        machineLog.setTaskSituation(TaskSituation.PENDENTE);
        machineLog.setMachine(maintenanceRequest.getMachine());
        machineLog.setMaintenanceRequest(maintenanceRequest);
        machineLog.setServicePerformed("Ocorrência de manutenção registrada.");
        machineLog.setResponsibleTeacher(maintenanceRequest.getNotifiedTeacher());
        machineLog.setTaskCriticality(taskCriticalityFor(maintenanceRequest.getPriority()));
        machineLog.setPlace(maintenanceRequest.getPlace());
        machineLog.setMaintenanceType(MaintenanceType.CORRETIVA);
        machineLog.setCreatedBy(creator);
        machineLogRepository.save(machineLog);
    }

    private TaskCriticality taskCriticalityFor(Priority priority) {
        return switch (priority) {
            case BAIXA -> TaskCriticality.BAIXA;
            case MEDIA -> TaskCriticality.MEDIA;
            case ALTA -> TaskCriticality.ALTA;
        };
    }
    @Transactional(readOnly = true)
    public Page<MaintenanceRequestResponse> getAll(
            String authenticatedEmail,
            String search,
            MaintenanceRequestStatus status,
            Priority priority,
            Pageable pageable
    ) {
        User user = authenticatedUser(authenticatedEmail);
        Specification<MaintenanceRequest> accessScope = (root, query, builder) -> {
            if (user.getRole() == Role.ADMIN || user.getRole() == Role.COORDENADOR) {
                return builder.conjunction();
            }
            if (user.getRole() == Role.ALUNO) {
                query.distinct(true);
                return builder.or(
                        builder.equal(root.get("createdBy").get("id"), user.getId()),
                        builder.equal(root.join("assignedStudents").get("id"), user.getId())
                );
            }

            Teacher teacher = (Teacher) user;
            List<UUID> classGroupIds = teacher.getClassGroups().stream()
                    .map(classGroup -> classGroup.getId())
                    .toList();
            List<Predicate> related = new java.util.ArrayList<>();
            related.add(builder.equal(root.get("createdBy").get("id"), user.getId()));
            related.add(builder.equal(root.get("notifiedTeacher").get("id"), user.getId()));
            if (!classGroupIds.isEmpty()) {
                query.distinct(true);
                related.add(root.join("assignedStudents")
                        .join("classGroups")
                        .get("id")
                        .in(classGroupIds));
            }
            return builder.or(related.toArray(Predicate[]::new));
        };
        Specification<MaintenanceRequest> filters = accessScope;

        if (search != null && !search.isBlank()) {
            String pattern = "%" + search.trim().toLowerCase(java.util.Locale.ROOT) + "%";
            filters = filters.and((root, query, builder) -> builder.or(
                    builder.like(builder.lower(root.get("description")), pattern),
                    builder.like(builder.lower(root.get("machine").get("name")), pattern),
                    builder.like(builder.lower(root.get("place").get("name")), pattern),
                    builder.like(builder.lower(root.get("notifiedTeacher").get("name")), pattern)
            ));
        }
        if (status != null) {
            filters = filters.and((root, query, builder) ->
                    builder.equal(root.get("status"), status));
        }
        if (priority != null) {
            filters = filters.and((root, query, builder) ->
                    builder.equal(root.get("priority"), priority));
        }

        return maintenanceRepository.findAll(filters, pageable)
                .map(maintenanceRequestMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public MaintenanceRequestResponse getById(UUID id, String authenticatedEmail) {
        MaintenanceRequest maintenanceRequest = findById(id);
        User user = authenticatedUser(authenticatedEmail);
        if (!canAccess(user, maintenanceRequest)) {
            throw new AccessDeniedException("Você não possui acesso a esta solicitação de manutenção.");
        }
        return maintenanceRequestMapper.toResponse(maintenanceRequest);
    }

    @Transactional
    public MaintenanceRequestResponse approve(
            UUID id,
            MaintenanceApprovalRequest request,
            String authenticatedEmail,
            ClientRequestMetadata metadata
    ) {
        MaintenanceRequest maintenanceRequest = maintenanceRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitação de manutenção", id));
        User authenticatedUser = authenticatedUser(authenticatedEmail);
        boolean canDecide = authenticatedUser.getRole() == Role.ADMIN
                || authenticatedUser.getRole() == Role.COORDENADOR
                || (authenticatedUser instanceof Teacher teacher
                && isRelatedTeacher(teacher, maintenanceRequest));
        if (!canDecide) {
            throw new AccessDeniedException(
                    "Somente um professor relacionado, coordenador ou administrador pode decidir esta solicitação."
            );
        }
        if (maintenanceRequest.getStatus() != MaintenanceRequestStatus.PENDENTE_APROVACAO_PROFESSOR) {
            throw new InvalidStateException("A solicitação já recebeu uma decisão do professor.");
        }

        boolean approved = request.approved();
        maintenanceRequest.setStatus(approved
                ? MaintenanceRequestStatus.PENDENTE_APROVACAO_COORDENADOR
                : MaintenanceRequestStatus.REPROVADA_PELO_PROFESSOR);
        maintenanceRequest.setApprovedBy(authenticatedUser);
        maintenanceRequest.setApprovedAt(LocalDateTime.now());
        maintenanceRequest.setRejectionReason(approved ? null : normalizeReason(request.reason()));
        if (approved) {
            maintenanceRequest.setWorkOrderNumber(workOrderNumber(maintenanceRequest));
            maintenanceRequest.setWorkOrderCreatedAt(LocalDateTime.now());
            maintenanceRequest.setWorkOrderCreatedBy(authenticatedUser);
        }
        maintenanceRequest = maintenanceRepository.save(maintenanceRequest);

        auditService.recordInCurrentTransaction(
                authenticatedUser,
                approved ? "MAINTENANCE_REQUEST_APPROVED" : "MAINTENANCE_REQUEST_REJECTED",
                "MAINTENANCE_REQUEST",
                maintenanceRequest.getId(),
                metadata.endpoint(),
                metadata.httpMethod(),
                metadata.ipAddress(),
                metadata.userAgent(),
                true,
                approved ? "Solicitação aprovada pelo professor notificado."
                        : "Solicitação reprovada pelo professor notificado."
        );
        notificationService.notifyUser(
                maintenanceRequest.getCreatedBy(),
                approved ? "Solicitação aprovada" : "Solicitação reprovada",
                "Decisão do professor",
                approved
                        ? "Sua solicitação de manutenção foi aprovada por " + authenticatedUser.getName() + "."
                        : rejectionMessage(authenticatedUser, maintenanceRequest.getRejectionReason())
        );
        if (approved) {
            notifyCoordinators(maintenanceRequest);
        }
        return maintenanceRequestMapper.toResponse(maintenanceRequest);
    }

    @Transactional
    public MaintenanceRequestResponse approveWorkOrder(
            UUID id,
            MaintenanceApprovalRequest request,
            String authenticatedEmail,
            ClientRequestMetadata metadata
    ) {
        MaintenanceRequest maintenanceRequest = maintenanceRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitação de manutenção", id));
        User coordinator = authenticatedUser(authenticatedEmail);
        if (coordinator.getRole() != Role.COORDENADOR && coordinator.getRole() != Role.ADMIN) {
            throw new AccessDeniedException(
                    "Somente coordenadores ou administradores podem decidir uma ordem de manutenção."
            );
        }
        if (maintenanceRequest.getStatus() != MaintenanceRequestStatus.PENDENTE_APROVACAO_COORDENADOR) {
            throw new InvalidStateException("A ordem de manutenção não está pendente de aprovação do coordenador.");
        }

        boolean approved = request.approved();
        maintenanceRequest.setStatus(approved
                ? MaintenanceRequestStatus.APROVADA_PELO_COORDENADOR
                : MaintenanceRequestStatus.REPROVADA_PELO_COORDENADOR);
        maintenanceRequest.setCoordinatorApprovedBy(coordinator);
        maintenanceRequest.setCoordinatorApprovedAt(LocalDateTime.now());
        maintenanceRequest.setCoordinatorRejectionReason(approved ? null : normalizeReason(request.reason()));
        maintenanceRequest = maintenanceRepository.save(maintenanceRequest);

        auditService.recordInCurrentTransaction(
                coordinator,
                approved ? "MAINTENANCE_WORK_ORDER_APPROVED" : "MAINTENANCE_WORK_ORDER_REJECTED",
                "MAINTENANCE_REQUEST",
                maintenanceRequest.getId(),
                metadata.endpoint(),
                metadata.httpMethod(),
                metadata.ipAddress(),
                metadata.userAgent(),
                true,
                approved ? "Ordem de manutenção aprovada pelo coordenador."
                        : "Ordem de manutenção reprovada pelo coordenador."
        );
        notificationService.notifyUser(
                maintenanceRequest.getCreatedBy(),
                approved ? "Ordem de manutenção aprovada" : "Ordem de manutenção reprovada",
                "Decisão do coordenador",
                approved
                        ? "A ordem " + maintenanceRequest.getWorkOrderNumber() + " foi aprovada pelo coordenador."
                        : coordinatorRejectionMessage(coordinator, maintenanceRequest.getCoordinatorRejectionReason())
        );
        notificationService.notifyUser(
                maintenanceRequest.getNotifiedTeacher(),
                approved ? "Ordem de manutenção aprovada" : "Ordem de manutenção reprovada",
                "Decisão do coordenador",
                approved
                        ? "A ordem " + maintenanceRequest.getWorkOrderNumber() + " foi aprovada pelo coordenador."
                        : coordinatorRejectionMessage(coordinator, maintenanceRequest.getCoordinatorRejectionReason())
        );
        return maintenanceRequestMapper.toResponse(maintenanceRequest);
    }

    @Transactional
    public MaintenanceRequestResponse update(
            UUID id,
            MaintenanceRequestRequest request,
            String authenticatedEmail
    ) {
        User editor = requireManager(authenticatedEmail);
        MaintenanceRequest maintenanceRequest = findById(id);
        maintenanceRequest.setSector(Sector.valueOf(request.sector().trim().toUpperCase(java.util.Locale.ROOT)));
        maintenanceRequest.setPriority(Priority.valueOf(request.priority().trim().toUpperCase(java.util.Locale.ROOT)));
        maintenanceRequest.setDescription(request.description());
        maintenanceRequest.setPlace(references.place(request.placeId()));
        maintenanceRequest.setNotifiedTeacher(references.teacher(request.notifiedTeacherId()));
        maintenanceRequest.setMachine(references.machine(request.machineId()));
        maintenanceRequest.getMedia().clear();
        maintenanceRequest.getMedia().addAll(imageMediaFactory.fromDataUrls(
                request.images(), editor, MediaType.MAINTENANCE_REQUEST,
                "ocorrencia", "Evidência da ocorrência", true
        ));
        return maintenanceRequestMapper.toResponse(maintenanceRepository.save(maintenanceRequest));
    }

    @Transactional
    public MaintenanceRequestResponse patch(
            UUID id,
            MaintenanceRequestPatchRequest request,
            String authenticatedEmail
    ) {
        requireManager(authenticatedEmail);
        MaintenanceRequest maintenanceRequest = findById(id);
        if (request.sector() != null) {
            maintenanceRequest.setSector(Sector.valueOf(request.sector().trim().toUpperCase(java.util.Locale.ROOT)));
        }
        if (request.priority() != null) {
            maintenanceRequest.setPriority(Priority.valueOf(request.priority().trim().toUpperCase(java.util.Locale.ROOT)));
        }
        if (request.description() != null) {
            maintenanceRequest.setDescription(request.description());
        }
        return maintenanceRequestMapper.toResponse(maintenanceRepository.save(maintenanceRequest));
    }

    @Transactional
    public void delete(UUID id, String authenticatedEmail) {
        requireManager(authenticatedEmail);
        maintenanceRepository.delete(findById(id));
    }

    private MaintenanceRequest findById(UUID id) {
        return maintenanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitação de manutenção", id));
    }

    private User authenticatedUser(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário autenticado"));
    }

    private User requireManager(String authenticatedEmail) {
        User user = authenticatedUser(authenticatedEmail);
        if (user.getRole() != Role.ADMIN && user.getRole() != Role.COORDENADOR) {
            throw new AccessDeniedException(
                    "Apenas coordenadores ou administradores podem alterar solicitações de manutenção."
            );
        }
        return user;
    }

    private boolean canAccess(User user, MaintenanceRequest maintenanceRequest) {
        return switch (user.getRole()) {
            case ADMIN -> true;
            case ALUNO -> maintenanceRequest.getCreatedBy().getId().equals(user.getId())
                    || maintenanceRequest.getAssignedStudents().stream()
                    .anyMatch(student -> student.getId().equals(user.getId()));
            case PROFESSOR -> isRelatedTeacher((Teacher) user, maintenanceRequest);
            case COORDENADOR -> true;
        };
    }

    private boolean isRelatedTeacher(Teacher teacher, MaintenanceRequest maintenanceRequest) {
        if (maintenanceRequest.getCreatedBy().getId().equals(teacher.getId())
                || maintenanceRequest.getNotifiedTeacher().getId().equals(teacher.getId())) {
            return true;
        }
        java.util.Set<UUID> teacherClassGroupIds = teacher.getClassGroups().stream()
                .map(classGroup -> classGroup.getId())
                .collect(java.util.stream.Collectors.toSet());
        return maintenanceRequest.getAssignedStudents().stream()
                .flatMap(student -> student.getClassGroups().stream())
                .anyMatch(classGroup -> teacherClassGroupIds.contains(classGroup.getId()));
    }

    private String workOrderNumber(MaintenanceRequest maintenanceRequest) {
        return "OS-" + LocalDateTime.now().getYear() + "-"
                + maintenanceRequest.getId().toString().substring(0, 8).toUpperCase(java.util.Locale.ROOT);
    }

    private void notifyCoordinators(MaintenanceRequest maintenanceRequest) {
        userRepository.findAllByRoleAndEnabledTrueAndAccountNonLockedTrue(Role.COORDENADOR)
                .forEach(coordinator -> notificationService.notifyUser(
                        coordinator,
                        "Ordem de manutenção aguardando aprovação",
                        "Ordem " + maintenanceRequest.getWorkOrderNumber(),
                        "Uma ordem de manutenção foi gerada e aguarda sua aprovação."
                ));
    }

    private String normalizeReason(String reason) {
        return reason == null || reason.isBlank() ? null : reason.trim();
    }

    private String rejectionMessage(User teacher, String reason) {
        String message = "Sua solicitação de manutenção foi reprovada por " + teacher.getName() + ".";
        return reason == null ? message : message + " Motivo: " + reason;
    }

    private String coordinatorRejectionMessage(User coordinator, String reason) {
        String message = "A ordem de manutenção foi reprovada por " + coordinator.getName() + ".";
        return reason == null ? message : message + " Motivo: " + reason;
    }

}
