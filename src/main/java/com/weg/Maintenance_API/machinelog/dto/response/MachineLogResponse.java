package com.weg.Maintenance_API.machinelog.dto.response;

import com.weg.Maintenance_API.enums.MaintenanceType;
import com.weg.Maintenance_API.enums.TaskCriticality;
import com.weg.Maintenance_API.enums.TaskSituation;

import java.time.LocalDateTime;
import java.util.List;

public record MachineLogResponse(
        Long id,
        String numberCard,
        String title,
        String description,
        String executionReport,
        TaskSituation taskSituation,
        Long machineId,
        String machineName,
        String servicePerformed,
        LocalDateTime teacherConcludedAt,
        Long responsibleTeacherId,
        String responsibleTeacherName,
        LocalDateTime registeredAt,
        LocalDateTime executionStartedAt,
        LocalDateTime executionEndedAt,
        String plannedAction,
        TaskCriticality taskCriticality,
        Long placeId,
        String placeName,
        MaintenanceType maintenanceType,
        Long classGroupId,
        String classGroupAcronym,
        List<Long> assignedStudentIds,
        String reportLink
) {
}
