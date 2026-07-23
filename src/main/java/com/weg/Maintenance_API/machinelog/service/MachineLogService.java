package com.weg.Maintenance_API.machinelog.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.weg.Maintenance_API.machinelog.dto.request.MachineLogPatchRequest;
import com.weg.Maintenance_API.machinelog.dto.request.MachineLogRequest;
import com.weg.Maintenance_API.machinelog.dto.response.MachineLogResponse;
import com.weg.Maintenance_API.machinelog.entity.MachineLog;
import com.weg.Maintenance_API.machinelog.mapper.MachineLogMapper;
import com.weg.Maintenance_API.machinelog.repository.MachineLogRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MachineLogService {

    private final MachineLogRepository machineLogRepository;
    private final MachineLogMapper machineLogMapper;

    @Transactional
    public MachineLogResponse save(MachineLogRequest request) {
        MachineLog machineLog = machineLogMapper.toEntity(request);
        machineLog = machineLogRepository.save(machineLog);
        return machineLogMapper.toResponse(machineLog);
    }

    @Transactional(readOnly = true)
    public List<MachineLogResponse> getAll() {
        return machineLogRepository.findAll().stream().map(machineLogMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public MachineLogResponse getById(Long id) {
        MachineLog machineLog = machineLogRepository.findById(id).orElseThrow(() -> new RuntimeException(""));
        return machineLogMapper.toResponse(machineLog);
    }

    @Transactional
    public MachineLogResponse update(Long id, MachineLogRequest request) {
        MachineLog machineLog = machineLogRepository.findById(id).orElseThrow(() -> new RuntimeException(""));
        machineLog.setTitle(request.title());
        machineLog.setDescription(request.description());
        machineLog.setExecutionReport(request.executionReport());
        machineLog.setTaskSituation(request.taskSituation());
        machineLog.setServicePerformed(request.servicePerformed());
        machineLog.setTeacherConcludedAt(request.teacherConcludedAt());
        machineLog.setExecutionStartedAt(request.executionStartedAt());
        machineLog.setExecutionEndedAt(request.executionEndedAt());
        machineLog.setPlannedAction(request.plannedAction());
        machineLog.setTaskCriticality(request.taskCriticality());
        machineLog.setMaintenanceType(request.maintenanceType());
        machineLog.setReportLink(request.reportLink());
        return machineLogMapper.toResponse(machineLogRepository.save(machineLog));
    }

    @Transactional
    public MachineLogResponse patch(Long id, MachineLogPatchRequest request) {
        MachineLog machineLog = machineLogRepository.findById(id).orElseThrow(() -> new RuntimeException(""));

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
            machineLog.setTaskSituation(request.taskSituation());
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
            machineLog.setTaskCriticality(request.taskCriticality());
        }
        if (request.maintenanceType() != null) {
            machineLog.setMaintenanceType(request.maintenanceType());
        }
        if (request.reportLink() != null) {
            machineLog.setReportLink(request.reportLink());
        }

        return machineLogMapper.toResponse(machineLogRepository.save(machineLog));
    }

    @Transactional
    public void delete(Long id) {
        machineLogRepository.deleteById(id);
    }
}