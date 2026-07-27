package com.weg.Maintenance_API.machinelog.service;


import java.util.UUID;

import com.weg.Maintenance_API.enums.MaintenanceType;
import com.weg.Maintenance_API.enums.TaskCriticality;
import com.weg.Maintenance_API.enums.TaskSituation;
import com.weg.Maintenance_API.exception.type.ResourceNotFoundException;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.weg.Maintenance_API.machinelog.dto.request.MachineLogPatchRequest;
import com.weg.Maintenance_API.machinelog.dto.request.MachineLogRequest;
import com.weg.Maintenance_API.machinelog.dto.response.MachineLogResponse;
import com.weg.Maintenance_API.machinelog.entity.MachineLog;
import com.weg.Maintenance_API.machinelog.mapper.MachineLogMapper;
import com.weg.Maintenance_API.machinelog.repository.MachineLogRepository;
import com.weg.Maintenance_API.service.EntityReferenceService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MachineLogService {

    private final MachineLogRepository machineLogRepository;
    private final MachineLogMapper machineLogMapper;
    private final EntityReferenceService references;

    // Cria e persiste os dados da operacao.
    @Transactional
    public MachineLogResponse save(MachineLogRequest request) {
        MachineLog machineLog = machineLogMapper.toEntity(request);
        machineLog = machineLogRepository.save(machineLog);
        return machineLogMapper.toResponse(machineLog);
    }

    // Busca os dados necessarios para esta operacao.
    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<MachineLogResponse> getAll(
            org.springframework.data.domain.Pageable pageable
    ) {
        return machineLogRepository.findAll(pageable).map(machineLogMapper::toResponse);
    }

    // Busca os dados necessarios para esta operacao.
    @Transactional(readOnly = true)
    public MachineLogResponse getById(UUID id) {
        MachineLog machineLog = machineLogRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Log de mÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Â ÃƒÂ¢Ã¢â€šÂ¬Ã¢â€žÂ¢ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã‚Â ÃƒÆ’Ã‚Â¢ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬ÃƒÂ¢Ã¢â‚¬Å¾Ã‚Â¢ÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã‚Â¢ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬Ãƒâ€¦Ã‚Â¡ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¡quina", id));
        return machineLogMapper.toResponse(machineLog);
    }

    // Atualiza o estado conforme os dados informados.
    @Transactional
    public MachineLogResponse update(UUID id, MachineLogRequest request) {
        MachineLog machineLog = machineLogRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Log de mÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Â ÃƒÂ¢Ã¢â€šÂ¬Ã¢â€žÂ¢ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã‚Â ÃƒÆ’Ã‚Â¢ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬ÃƒÂ¢Ã¢â‚¬Å¾Ã‚Â¢ÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã‚Â¢ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬Ãƒâ€¦Ã‚Â¡ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¡quina", id));
        applyReferences(machineLog, request);
        machineLog.setTitle(request.title());
        machineLog.setDescription(request.description());
        machineLog.setExecutionReport(request.executionReport());
        machineLog.setTaskSituation(TaskSituation.valueOf(request.taskSituation()));
        machineLog.setServicePerformed(request.servicePerformed());
        machineLog.setTeacherConcludedAt(request.teacherConcludedAt());
        machineLog.setExecutionStartedAt(request.executionStartedAt());
        machineLog.setExecutionEndedAt(request.executionEndedAt());
        machineLog.setPlannedAction(request.plannedAction());
        machineLog.setTaskCriticality(TaskCriticality.valueOf(request.taskCriticality()));
        machineLog.setMaintenanceType(MaintenanceType.valueOf(request.maintenanceType()));
        machineLog.setReportLink(request.reportLink());
        return machineLogMapper.toResponse(machineLogRepository.save(machineLog));
    }

    // Atualiza o estado conforme os dados informados.
    @Transactional
    public MachineLogResponse patch(UUID id, MachineLogPatchRequest request) {
        MachineLog machineLog = machineLogRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Log de mÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Â ÃƒÂ¢Ã¢â€šÂ¬Ã¢â€žÂ¢ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã‚Â ÃƒÆ’Ã‚Â¢ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬ÃƒÂ¢Ã¢â‚¬Å¾Ã‚Â¢ÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã‚Â¢ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬Ãƒâ€¦Ã‚Â¡ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¡quina", id));

        if (request.title() != null) {
            machineLog.setTitle(request.title());
        }
        if (request.description() != null) {
            machineLog.setDescription(request.description());
        }
        if (request.executionReport() != null) {
            machineLog.setExecutionReport(request.executionReport());
        }
        if (request.taskSituation() != null) {
            machineLog.setTaskSituation(TaskSituation.valueOf(
                    request.taskSituation().trim().toUpperCase(java.util.Locale.ROOT)
            ));
        }
        if (request.servicePerformed() != null) {
            machineLog.setServicePerformed(request.servicePerformed());
        }
        if (request.teacherConcludedAt() != null) {
            machineLog.setTeacherConcludedAt(request.teacherConcludedAt());
        }
        if (request.executionStartedAt() != null) {
            machineLog.setExecutionStartedAt(request.executionStartedAt());
        }
        if (request.executionEndedAt() != null) {
            machineLog.setExecutionEndedAt(request.executionEndedAt());
        }
        if (request.plannedAction() != null) {
            machineLog.setPlannedAction(request.plannedAction());
        }
        if (request.taskCriticality() != null) {
            machineLog.setTaskCriticality(TaskCriticality.valueOf(
                    request.taskCriticality().trim().toUpperCase(java.util.Locale.ROOT)
            ));
        }
        if (request.maintenanceType() != null) {
            machineLog.setMaintenanceType(MaintenanceType.valueOf(
                    request.maintenanceType().trim().toUpperCase(java.util.Locale.ROOT)
            ));
        }
        if (request.reportLink() != null) {
            machineLog.setReportLink(request.reportLink());
        }

        return machineLogMapper.toResponse(machineLogRepository.save(machineLog));
    }

    // Remove ou invalida os dados solicitados.
    @Transactional
    public void delete(UUID id) {
        machineLogRepository.delete(machineLogRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Log de mÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Â ÃƒÂ¢Ã¢â€šÂ¬Ã¢â€žÂ¢ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¡quina", id)));
    }
    // Aplica os dados recebidos na entidade.
    private void applyReferences(MachineLog entity, MachineLogRequest request) {
        entity.setMachine(references.machine(request.machineId()));
        entity.setResponsibleTeacher(request.responsibleTeacherId() == null ? null : references.teacher(request.responsibleTeacherId()));
        entity.setPlace(request.placeId() == null ? null : references.place(request.placeId()));
        entity.setClassGroup(request.classGroupId() == null ? null : references.classGroup(request.classGroupId()));
        entity.setAssignedStudents(request.assignedStudentIds() == null ? new java.util.ArrayList<>() : references.students(request.assignedStudentIds()));
    }
}
