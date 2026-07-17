package com.weg.Maintenance_API.machinelog.dto.response;

import com.weg.Maintenance_API.enums.MaintenanceType;
import com.weg.Maintenance_API.enums.TaskCriticality;
import com.weg.Maintenance_API.enums.TaskSituation;

import java.time.LocalDateTime;
import java.util.List;

public record MachineLogResponse(
        Long id,
        String title,
        String description,
        String content,
        TaskSituation taskSituation,
        Integer machineId,
        String machineName,
        String serviceExecute,
        LocalDateTime conclusion,
        Long teacherId,
        String teacherName,
        LocalDateTime createdAt,
        LocalDateTime scheduledFor,
        LocalDateTime requestedAt,
        String actionToExecute,
        TaskCriticality taskCriticality,
        byte[] image,
        Long placeId,
        String placeName,
        MaintenanceType maintenanceType,
        String registrationPeriod,
        Long classGroupId,
        String classGroupAcronym,
        List<Long> assignedStudentIds,
        String reportLink) {
}

