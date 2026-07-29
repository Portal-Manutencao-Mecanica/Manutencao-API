package com.weg.Maintenance_API.machinelog.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import com.weg.Maintenance_API.enums.MaintenanceType;
import com.weg.Maintenance_API.enums.TaskCriticality;
import com.weg.Maintenance_API.enums.TaskSituation;
import com.weg.Maintenance_API.exception.type.ResourceNotFoundException;
import com.weg.Maintenance_API.machinelog.dto.request.MachineLogPatchRequest;
import com.weg.Maintenance_API.machinelog.dto.request.MachineLogRequest;
import com.weg.Maintenance_API.machinelog.dto.response.MachineLogResponse;
import com.weg.Maintenance_API.machinelog.entity.MachineLog;
import com.weg.Maintenance_API.machinelog.mapper.MachineLogMapper;
import com.weg.Maintenance_API.machinelog.repository.MachineLogRepository;
import com.weg.Maintenance_API.notification.service.NotificationService;
import com.weg.Maintenance_API.service.EntityReferenceService;
import com.weg.Maintenance_API.user.UserRepository;
import com.weg.Maintenance_API.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MachineLogService {

    private final MachineLogRepository machineLogRepository;
    private final MachineLogMapper machineLogMapper;
    private final EntityReferenceService references;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Transactional
    public MachineLogResponse save(MachineLogRequest request, String authenticatedEmail) {
        MachineLog machineLog = machineLogMapper.toEntity(request);
        applyReferences(machineLog, request);
        machineLog.setCreatedBy(authenticatedUser(authenticatedEmail));
        machineLog = machineLogRepository.save(machineLog);
        notifyInvolved(machineLog, "Novo log de máquina", "Um novo log foi registrado para a máquina " + machineLog.getMachine().getName() + ".");
        return machineLogMapper.toResponse(machineLog);
    }

    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<MachineLogResponse> getAll(org.springframework.data.domain.Pageable pageable) {
        return machineLogRepository.findAll(pageable).map(machineLogMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public MachineLogResponse getById(UUID id) {
        return machineLogMapper.toResponse(findById(id));
    }

    @Transactional
    public MachineLogResponse update(UUID id, MachineLogRequest request) {
        MachineLog machineLog = findById(id);
        applyReferences(machineLog, request);
        machineLog.setTitle(request.title());
        machineLog.setDescription(request.description());
        machineLog.setExecutionReport(request.executionReport());
        machineLog.setTaskSituation(TaskSituation.valueOf(request.taskSituation().trim().toUpperCase(java.util.Locale.ROOT)));
        machineLog.setServicePerformed(request.servicePerformed());
        machineLog.setTeacherConcludedAt(request.teacherConcludedAt());
        machineLog.setExecutionStartedAt(request.executionStartedAt());
        machineLog.setExecutionEndedAt(request.executionEndedAt());
        machineLog.setPlannedAction(request.plannedAction());
        machineLog.setTaskCriticality(TaskCriticality.valueOf(request.taskCriticality().trim().toUpperCase(java.util.Locale.ROOT)));
        machineLog.setMaintenanceType(request.maintenanceType() == null ? null : MaintenanceType.valueOf(request.maintenanceType().trim().toUpperCase(java.util.Locale.ROOT)));
        machineLog.setReportLink(request.reportLink());
        machineLog = machineLogRepository.save(machineLog);
        notifyInvolved(machineLog, "Log de máquina atualizado", "O log da máquina " + machineLog.getMachine().getName() + " foi atualizado.");
        return machineLogMapper.toResponse(machineLog);
    }

    @Transactional
    public MachineLogResponse patch(UUID id, MachineLogPatchRequest request) {
        MachineLog machineLog = findById(id);
        if (request.title() != null) machineLog.setTitle(request.title());
        if (request.description() != null) machineLog.setDescription(request.description());
        if (request.executionReport() != null) machineLog.setExecutionReport(request.executionReport());
        if (request.taskSituation() != null) machineLog.setTaskSituation(TaskSituation.valueOf(request.taskSituation().trim().toUpperCase(java.util.Locale.ROOT)));
        if (request.servicePerformed() != null) machineLog.setServicePerformed(request.servicePerformed());
        if (request.teacherConcludedAt() != null) machineLog.setTeacherConcludedAt(request.teacherConcludedAt());
        if (request.executionStartedAt() != null) machineLog.setExecutionStartedAt(request.executionStartedAt());
        if (request.executionEndedAt() != null) machineLog.setExecutionEndedAt(request.executionEndedAt());
        if (request.plannedAction() != null) machineLog.setPlannedAction(request.plannedAction());
        if (request.taskCriticality() != null) machineLog.setTaskCriticality(TaskCriticality.valueOf(request.taskCriticality().trim().toUpperCase(java.util.Locale.ROOT)));
        if (request.maintenanceType() != null) machineLog.setMaintenanceType(MaintenanceType.valueOf(request.maintenanceType().trim().toUpperCase(java.util.Locale.ROOT)));
        if (request.reportLink() != null) machineLog.setReportLink(request.reportLink());
        machineLog = machineLogRepository.save(machineLog);
        notifyInvolved(machineLog, "Log de máquina atualizado", "O log da máquina " + machineLog.getMachine().getName() + " foi atualizado.");
        return machineLogMapper.toResponse(machineLog);
    }

    @Transactional
    public void delete(UUID id) {
        machineLogRepository.delete(findById(id));
    }

    private MachineLog findById(UUID id) {
        return machineLogRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Log de máquina", id));
    }

    private User authenticatedUser(String email) {
        return userRepository.findByEmailIgnoreCase(email).orElseThrow(() -> new ResourceNotFoundException("Usuário autenticado"));
    }

    private void applyReferences(MachineLog entity, MachineLogRequest request) {
        entity.setMachine(references.machine(request.machineId()));
        entity.setResponsibleTeacher(request.responsibleTeacherId() == null ? null : references.teacher(request.responsibleTeacherId()));
        entity.setPlace(request.placeId() == null ? null : references.place(request.placeId()));
        entity.setClassGroup(request.classGroupId() == null ? null : references.classGroup(request.classGroupId()));
        entity.setAssignedStudents(request.assignedStudentIds() == null ? List.of() : references.students(request.assignedStudentIds()));
    }

    private void notifyInvolved(MachineLog machineLog, String title, String description) {
        LinkedHashMap<UUID, User> recipients = new LinkedHashMap<>();
        if (machineLog.getResponsibleTeacher() != null) recipients.put(machineLog.getResponsibleTeacher().getId(), machineLog.getResponsibleTeacher());
        machineLog.getAssignedStudents().forEach(student -> recipients.put(student.getId(), student));
        recipients.values().forEach(user -> notificationService.notifyUser(user, title, "Atualização de máquina", description));
    }
}