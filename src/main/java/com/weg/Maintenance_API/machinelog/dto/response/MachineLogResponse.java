package com.weg.Maintenance_API.machinelog.dto.response;


import java.util.UUID;

import com.weg.Maintenance_API.enums.MaintenanceType;
import com.weg.Maintenance_API.enums.TaskCriticality;
import com.weg.Maintenance_API.enums.TaskSituation;

import java.time.LocalDateTime;
import java.util.List;

// Executa a operacao deste metodo.
public record MachineLogResponse(
        UUID id,
        String title,
        String description,
        String executionReport,
        TaskSituation taskSituation,
        UUID machineId,
        String machineName,
        UUID maintenanceRequestId,
        String servicePerformed,
        LocalDateTime teacherConcludedAt,
        UUID responsibleTeacherId,
        String responsibleTeacherName,
        LocalDateTime registeredAt,
        LocalDateTime executionStartedAt,
        LocalDateTime executionEndedAt,
        String plannedAction,
        TaskCriticality taskCriticality,
        UUID placeId,
        String placeName,
        MaintenanceType maintenanceType,
        UUID classGroupId,
        String classGroupAcronym,
        List<UUID> assignedStudentIds,
        String reportLink
) {
}
