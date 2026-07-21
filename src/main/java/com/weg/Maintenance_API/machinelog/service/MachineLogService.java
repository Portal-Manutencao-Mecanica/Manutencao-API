package com.weg.Maintenance_API.machinelog.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public MachineLogResponse save(MachineLogRequest machineLogRequest) {
        MachineLog machineLog = machineLogMapper.toEntity(machineLogRequest);

        machineLog = machineLogRepository.save(machineLog);

        return machineLogMapper.toResponse(machineLog);
    }

    @Transactional(readOnly = true)
    public List<MachineLogResponse> getAll() {
        List<MachineLog> machineLogs = machineLogRepository.findAll();

        return machineLogs.stream().map(machineLogMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public MachineLogResponse getById(Long id) {
        MachineLog machineLog = machineLogRepository.findById(id).orElseThrow(() -> new RuntimeException(""));

        return machineLogMapper.toResponse(machineLog);
    }

    @Transactional
    public MachineLogResponse update(Long id, MachineLogRequest machineLogRequest) {
        MachineLog machineLog = machineLogRepository.findById(id).orElseThrow(() -> new RuntimeException(""));

        machineLog.setTitle(machineLogRequest.title());
        machineLog.setDescription(machineLogRequest.description());
        machineLog.setExecutionReport(machineLogRequest.executionReport());
        machineLog.setTaskSituation(machineLogRequest.taskSituation());
        machineLog.setServicePerformed(machineLogRequest.servicePerformed());
        machineLog.setTeacherConcludedAt(machineLogRequest.teacherConcludedAt());
        machineLog.setExecutionStartedAt(machineLogRequest.executionStartedAt());
        machineLog.setExecutionEndedAt(machineLogRequest.executionEndedAt());
        machineLog.setPlannedAction(machineLogRequest.plannedAction());
        machineLog.setTaskCriticality(machineLogRequest.taskCriticality());
        machineLog.setMaintenanceType(machineLogRequest.maintenanceType());
        machineLog.setReportLink(machineLogRequest.reportLink());

        machineLog = machineLogRepository.save(machineLog);

        return machineLogMapper.toResponse(machineLog);
    }

    @Transactional
    public void delete(Long id) {
        machineLogRepository.deleteById(id);
    }
}
