package com.weg.Maintenance_API.maintenancerequest.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.weg.Maintenance_API.audit.service.AuditService;
import com.weg.Maintenance_API.auth.service.ClientRequestMetadata;
import com.weg.Maintenance_API.enums.MaintenanceRequestStatus;
import com.weg.Maintenance_API.enums.Priority;
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

@Service
@RequiredArgsConstructor
public class MaintenanceRequestService {

    private final MaintenanceRepository maintenanceRepository;
    private final MaintenanceRequestMapper maintenanceRequestMapper;
    private final EntityReferenceService references;
    private final UserRepository userRepository;
    private final AuditService auditService;
    private final NotificationService notificationService;

    @Transactional
    public MaintenanceRequestResponse save(
            MaintenanceRequestRequest request,
            String authenticatedEmail
    ) {
        User creator = authenticatedUser(authenticatedEmail);
        if (!(creator instanceof Student student)) {
            throw new AccessDeniedException("Apenas alunos podem criar solicitações de manutenção.");
        }

        MaintenanceRequest maintenanceRequest = maintenanceRequestMapper.toEntity(request);
        maintenanceRequest.setCreatedBy(creator);
        maintenanceRequest.setAssignedStudents(List.of(student));
        maintenanceRequest.setPlace(references.place(request.placeId()));
        maintenanceRequest.setNotifiedTeacher(references.teacher(request.notifiedTeacherId()));
        maintenanceRequest.setMachine(references.machine(request.machineId()));
        maintenanceRequest.setStatus(MaintenanceRequestStatus.PENDENTE_APROVACAO_PROFESSOR);

        maintenanceRequest = maintenanceRepository.save(maintenanceRequest);
        notificationService.notifyUser(
                maintenanceRequest.getNotifiedTeacher(),
                "Nova solicitação de manutenção",
                "Solicitação aguardando aprovação",
                "Uma solicitação de manutenção foi enviada por " + creator.getName() + "."
        );
        return maintenanceRequestMapper.toResponse(maintenanceRequest);
    }

    @Transactional(readOnly = true)
    public List<MaintenanceRequestResponse> getAll() {
        return maintenanceRepository.findAll().stream().map(maintenanceRequestMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public MaintenanceRequestResponse getById(UUID id) {
        return maintenanceRequestMapper.toResponse(findById(id));
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
        if (!(authenticatedUser instanceof Teacher)
                || !maintenanceRequest.getNotifiedTeacher().getId().equals(authenticatedUser.getId())) {
            throw new AccessDeniedException("Somente o professor notificado pode decidir esta solicitação.");
        }
        if (maintenanceRequest.getStatus() != MaintenanceRequestStatus.PENDENTE_APROVACAO_PROFESSOR) {
            throw new InvalidStateException("A solicitação já recebeu uma decisão do professor.");
        }

        boolean approved = request.approved();
        maintenanceRequest.setStatus(approved
                ? MaintenanceRequestStatus.APROVADA_PELO_PROFESSOR
                : MaintenanceRequestStatus.REPROVADA_PELO_PROFESSOR);
        maintenanceRequest.setApprovedBy(authenticatedUser);
        maintenanceRequest.setApprovedAt(LocalDateTime.now());
        maintenanceRequest.setRejectionReason(approved ? null : normalizeReason(request.reason()));
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
        return maintenanceRequestMapper.toResponse(maintenanceRequest);
    }

    @Transactional
    public MaintenanceRequestResponse update(UUID id, MaintenanceRequestRequest request) {
        MaintenanceRequest maintenanceRequest = findById(id);
        maintenanceRequest.setSector(Sector.valueOf(request.sector().trim().toUpperCase(java.util.Locale.ROOT)));
        maintenanceRequest.setPriority(Priority.valueOf(request.priority().trim().toUpperCase(java.util.Locale.ROOT)));
        maintenanceRequest.setDescription(request.description());
        maintenanceRequest.setPlace(references.place(request.placeId()));
        maintenanceRequest.setNotifiedTeacher(references.teacher(request.notifiedTeacherId()));
        maintenanceRequest.setMachine(references.machine(request.machineId()));
        return maintenanceRequestMapper.toResponse(maintenanceRepository.save(maintenanceRequest));
    }

    @Transactional
    public MaintenanceRequestResponse patch(UUID id, MaintenanceRequestPatchRequest request) {
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
    public void delete(UUID id) {
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

    private String normalizeReason(String reason) {
        return reason == null || reason.isBlank() ? null : reason.trim();
    }

    private String rejectionMessage(User teacher, String reason) {
        String message = "Sua solicitação de manutenção foi reprovada por " + teacher.getName() + ".";
        return reason == null ? message : message + " Motivo: " + reason;
    }
}