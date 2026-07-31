package com.weg.Maintenance_API.maintenancerequest.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import com.weg.Maintenance_API.audit.service.AuditService;
import com.weg.Maintenance_API.admin.entity.Admin;
import com.weg.Maintenance_API.coordinator.entity.Coordinator;
import com.weg.Maintenance_API.auth.service.ClientRequestMetadata;
import com.weg.Maintenance_API.enums.MaintenanceRequestStatus;
import com.weg.Maintenance_API.enums.Role;
import com.weg.Maintenance_API.exception.type.ResourceNotFoundException;
import com.weg.Maintenance_API.maintenancerequest.dto.request.MaintenanceApprovalRequest;
import com.weg.Maintenance_API.maintenancerequest.dto.request.MaintenanceRequestRequest;
import com.weg.Maintenance_API.maintenancerequest.entity.MaintenanceRequest;
import com.weg.Maintenance_API.maintenancerequest.mapper.MaintenanceRequestMapper;
import com.weg.Maintenance_API.maintenancerequest.repository.MaintenanceRepository;
import com.weg.Maintenance_API.notification.service.NotificationService;
import com.weg.Maintenance_API.service.EntityReferenceService;
import com.weg.Maintenance_API.student.entity.Student;
import com.weg.Maintenance_API.teacher.entity.Teacher;
import com.weg.Maintenance_API.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

@ExtendWith(MockitoExtension.class)
class MaintenanceRequestApprovalServiceTest {

    @Mock private MaintenanceRepository repository;
    @Mock private MaintenanceRequestMapper mapper;
    @Mock private EntityReferenceService references;
    @Mock private UserRepository userRepository;
    @Mock private AuditService auditService;
    @Mock private NotificationService notificationService;
    @InjectMocks private MaintenanceRequestService service;

    private final UUID requestId = UUID.randomUUID();
    private Teacher notifiedTeacher;
    private Student requester;
    private MaintenanceRequest request;
    private final ClientRequestMetadata metadata = new ClientRequestMetadata("/api/solicitao-manutencao/x/aprovacao", "PATCH", "127.0.0.1", "test");

    @BeforeEach
    void setUp() {
        notifiedTeacher = new Teacher();
        notifiedTeacher.setId(UUID.randomUUID());
        notifiedTeacher.setName("Notified teacher");
        notifiedTeacher.setEmail("teacher@example.test");
        notifiedTeacher.setRole(Role.PROFESSOR);
        requester = new Student();
        requester.setId(UUID.randomUUID());
        requester.setName("Requester student");
        requester.setEmail("student@example.test");
        requester.setRole(Role.ALUNO);
        request = new MaintenanceRequest();
        request.setId(requestId);
        request.setNotifiedTeacher(notifiedTeacher);
        request.setCreatedBy(requester);
        request.setStatus(MaintenanceRequestStatus.PENDENTE_APROVACAO_PROFESSOR);
        lenient().when(repository.findByIdForUpdate(requestId)).thenReturn(Optional.of(request));
        lenient().when(repository.save(any(MaintenanceRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void notifiedTeacherCanApprovePendingRequest() {
        when(userRepository.findByEmailIgnoreCase(notifiedTeacher.getEmail())).thenReturn(Optional.of(notifiedTeacher));

        service.approve(requestId, new MaintenanceApprovalRequest(true, null), notifiedTeacher.getEmail(), metadata);

        assertEquals(MaintenanceRequestStatus.PENDENTE_APROVACAO_COORDENADOR, request.getStatus());
        assertEquals(notifiedTeacher, request.getApprovedBy());
        assertEquals(notifiedTeacher, request.getWorkOrderCreatedBy());
        verify(auditService).recordInCurrentTransaction(any(), any(), any(), any(), any(), any(), any(), any(), any(Boolean.class), any());
        verify(notificationService).notifyUser(any(), any(), any(), any());
    }

    @Test
    void anotherTeacherCannotApproveRequest() {
        Teacher anotherTeacher = new Teacher();
        anotherTeacher.setId(UUID.randomUUID());
        anotherTeacher.setEmail("another@example.test");
        when(userRepository.findByEmailIgnoreCase(anotherTeacher.getEmail())).thenReturn(Optional.of(anotherTeacher));

        assertThrows(AccessDeniedException.class, () -> service.approve(
                requestId, new MaintenanceApprovalRequest(false, "not mine"), anotherTeacher.getEmail(), metadata));
    }

    @Test
    void decisionCanOnlyHappenOnce() {
        request.setStatus(MaintenanceRequestStatus.APROVADA_PELO_PROFESSOR);
        when(userRepository.findByEmailIgnoreCase(notifiedTeacher.getEmail())).thenReturn(Optional.of(notifiedTeacher));

        assertThrows(com.weg.Maintenance_API.exception.type.InvalidStateException.class, () -> service.approve(
                requestId, new MaintenanceApprovalRequest(true, null), notifiedTeacher.getEmail(), metadata));
    }

    @Test
    void missingRequestReturnsNotFound() {
        UUID missingId = UUID.randomUUID();
        when(repository.findByIdForUpdate(missingId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.approve(
                missingId, new MaintenanceApprovalRequest(true, null), notifiedTeacher.getEmail(), metadata));
    }
    @Test
    void authenticatedNonStudentCanCreateRequest() {
        MaintenanceRequestRequest input = new MaintenanceRequestRequest(
                "CENTRO_WEG", "MEDIA", UUID.randomUUID(), "Broken machine",
                notifiedTeacher.getId(), UUID.randomUUID(), java.util.List.of("data:image/png;base64,aQ==")
        );
        MaintenanceRequest created = new MaintenanceRequest();
        when(userRepository.findByEmailIgnoreCase(notifiedTeacher.getEmail())).thenReturn(Optional.of(notifiedTeacher));
        when(mapper.toEntity(input)).thenReturn(created);
        when(repository.save(created)).thenReturn(created);

        service.save(input, notifiedTeacher.getEmail());

        assertEquals(notifiedTeacher, created.getCreatedBy());
        assertEquals(java.util.List.of(), created.getAssignedStudents());
        assertEquals(1, created.getMedia().size());
        assertEquals("data:image/png;base64,aQ==", created.getMedia().getFirst().getImage());
        assertEquals(MaintenanceRequestStatus.PENDENTE_APROVACAO_PROFESSOR, created.getStatus());
    }

    @Test
    void nonAdminCannotUpdateRequest() {
        MaintenanceRequestRequest input = new MaintenanceRequestRequest(
                "CENTRO_WEG", "MEDIA", UUID.randomUUID(), "Updated description",
                notifiedTeacher.getId(), UUID.randomUUID(), java.util.List.of()
        );
        when(userRepository.findByEmailIgnoreCase(notifiedTeacher.getEmail())).thenReturn(Optional.of(notifiedTeacher));

        assertThrows(AccessDeniedException.class, () ->
                service.update(requestId, input, notifiedTeacher.getEmail()));
    }

    @Test
    void adminCanDeleteRequest() {
        Admin admin = new Admin();
        admin.setEmail("admin@example.test");
        admin.setRole(Role.ADMIN);
        when(userRepository.findByEmailIgnoreCase(admin.getEmail())).thenReturn(Optional.of(admin));
        when(repository.findById(requestId)).thenReturn(Optional.of(request));

        service.delete(requestId, admin.getEmail());

        verify(repository).delete(request);
    }

    @Test
    void coordinatorCanApproveGeneratedWorkOrder() {
        Coordinator coordinator = new Coordinator();
        coordinator.setId(UUID.randomUUID());
        coordinator.setEmail("coordinator@example.test");
        coordinator.setRole(Role.COORDENADOR);
        request.setStatus(MaintenanceRequestStatus.PENDENTE_APROVACAO_COORDENADOR);
        request.setWorkOrderNumber("OS-2026-00000001");
        when(userRepository.findByEmailIgnoreCase(coordinator.getEmail())).thenReturn(Optional.of(coordinator));

        service.approveWorkOrder(requestId, new MaintenanceApprovalRequest(true, null), coordinator.getEmail(), metadata);

        assertEquals(MaintenanceRequestStatus.APROVADA_PELO_COORDENADOR, request.getStatus());
        assertEquals(coordinator, request.getCoordinatorApprovedBy());
    }
}
