package com.weg.Maintenance_API.maintenancerequest.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.weg.Maintenance_API.audit.service.AuditService;
import com.weg.Maintenance_API.auth.service.ClientRequestMetadata;
import com.weg.Maintenance_API.enums.MaintenanceRequestStatus;
import com.weg.Maintenance_API.enums.Priority;
import com.weg.Maintenance_API.enums.Role;
import com.weg.Maintenance_API.enums.Sector;
import com.weg.Maintenance_API.exception.type.InvalidStateException;
import com.weg.Maintenance_API.exception.type.InvalidFileException;
import com.weg.Maintenance_API.exception.type.ResourceNotFoundException;
import com.weg.Maintenance_API.maintenancerequest.dto.request.MaintenanceApprovalRequest;
import com.weg.Maintenance_API.maintenancerequest.dto.request.MaintenanceRequestPatchRequest;
import com.weg.Maintenance_API.maintenancerequest.dto.request.MaintenanceRequestRequest;
import com.weg.Maintenance_API.maintenancerequest.dto.response.MaintenanceRequestResponse;
import com.weg.Maintenance_API.maintenancerequest.entity.MaintenanceRequest;
import com.weg.Maintenance_API.maintenancerequest.mapper.MaintenanceRequestMapper;
import com.weg.Maintenance_API.maintenancerequest.repository.MaintenanceRepository;
import com.weg.Maintenance_API.media.entity.Media;
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

    private static final int MAX_IMAGES = 5;
    private static final int MAX_IMAGE_BYTES = 5 * 1024 * 1024;
    private static final Pattern IMAGE_DATA_URL = Pattern.compile(
            "^data:(image/(?:png|jpeg|webp|svg\\+xml));base64,([A-Za-z0-9+/=]+)$"
    );

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
        MaintenanceRequest maintenanceRequest = maintenanceRequestMapper.toEntity(request);
        maintenanceRequest.setCreatedBy(creator);
        maintenanceRequest.setAssignedStudents(
                creator instanceof Student student ? List.of(student) : List.of()
        );
        maintenanceRequest.setPlace(references.place(request.placeId()));
        maintenanceRequest.setNotifiedTeacher(references.teacher(request.notifiedTeacherId()));
        maintenanceRequest.setMachine(references.machine(request.machineId()));
        maintenanceRequest.setMedia(imagesFrom(request.images(), creator));
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
    public List<MaintenanceRequestResponse> getAll(String authenticatedEmail) {
        User user = authenticatedUser(authenticatedEmail);
        List<MaintenanceRequest> requests = switch (user.getRole()) {
            case ADMIN -> maintenanceRepository.findAll();
            case ALUNO -> maintenanceRepository.findAllByCreatedById(user.getId());
            case PROFESSOR -> maintenanceRepository.findAllByNotifiedTeacherId(user.getId());
            case COORDENADOR -> maintenanceRepository.findAllByWorkOrderNumberIsNotNull();
        };
        return requests.stream().map(maintenanceRequestMapper::toResponse).toList();
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
        if (!(authenticatedUser instanceof Teacher)
                || !maintenanceRequest.getNotifiedTeacher().getId().equals(authenticatedUser.getId())) {
            throw new AccessDeniedException("Somente o professor notificado pode decidir esta solicitação.");
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
        if (coordinator.getRole() != Role.COORDENADOR) {
            throw new AccessDeniedException("Somente coordenadores podem decidir uma ordem de manutenção.");
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
        requireAdmin(authenticatedEmail);
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
    public MaintenanceRequestResponse patch(
            UUID id,
            MaintenanceRequestPatchRequest request,
            String authenticatedEmail
    ) {
        requireAdmin(authenticatedEmail);
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
        requireAdmin(authenticatedEmail);
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

    private void requireAdmin(String authenticatedEmail) {
        if (authenticatedUser(authenticatedEmail).getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Apenas administradores podem alterar solicitações de manutenção.");
        }
    }

    private boolean canAccess(User user, MaintenanceRequest maintenanceRequest) {
        return switch (user.getRole()) {
            case ADMIN -> true;
            case ALUNO -> maintenanceRequest.getCreatedBy().getId().equals(user.getId());
            case PROFESSOR -> maintenanceRequest.getNotifiedTeacher().getId().equals(user.getId());
            case COORDENADOR -> maintenanceRequest.getWorkOrderNumber() != null;
        };
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

    private List<Media> imagesFrom(List<String> images, User uploadedBy) {
        if (images == null || images.isEmpty()) {
            throw new InvalidFileException("Anexe pelo menos uma imagem da ocorrência.");
        }
        if (images.size() > MAX_IMAGES) {
            throw new InvalidFileException("Envie no máximo " + MAX_IMAGES + " imagens por ocorrência.");
        }

        List<Media> media = new ArrayList<>();
        for (int index = 0; index < images.size(); index++) {
            String image = images.get(index);
            if (image == null) {
                throw new InvalidFileException("A imagem enviada possui formato inválido.");
            }
            Matcher matcher = IMAGE_DATA_URL.matcher(image);
            if (!matcher.matches()) {
                throw new InvalidFileException("A imagem enviada possui formato inválido.");
            }

            byte[] bytes;
            try {
                bytes = Base64.getDecoder().decode(matcher.group(2));
            } catch (IllegalArgumentException exception) {
                throw new InvalidFileException("A imagem enviada possui Base64 inválido.", exception);
            }
            if (bytes.length == 0 || bytes.length > MAX_IMAGE_BYTES) {
                throw new InvalidFileException("Cada imagem deve ter no máximo 5 MB.");
            }

            Media item = new Media();
            item.setMediaType(com.weg.Maintenance_API.enums.MediaType.MAINTENANCE_REQUEST);
            item.setImage(image);
            item.setContentType(matcher.group(1));
            item.setOriginalName("ocorrencia-" + (index + 1) + extensionFor(matcher.group(1)));
            item.setFileSize((long) bytes.length);
            item.setDescription("Evidência da ocorrência");
            item.setUploadedBy(uploadedBy);
            item.setOrganization(uploadedBy.getOrganization());
            media.add(item);
        }
        return media;
    }

    private String extensionFor(String contentType) {
        return switch (contentType) {
            case "image/png" -> ".png";
            case "image/jpeg" -> ".jpg";
            case "image/webp" -> ".webp";
            default -> ".svg";
        };
    }
}
