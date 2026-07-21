package com.weg.Maintenance_API.machinelog.dto.request;

import com.weg.Maintenance_API.enums.MaintenanceType;
import com.weg.Maintenance_API.enums.TaskCriticality;
import com.weg.Maintenance_API.enums.TaskSituation;

import java.time.LocalDateTime;

public record MachineLogPatchRequest(
        String title,
        String description,
        String executionReport,
        TaskSituation taskSituation,
        String servicePerformed,
        LocalDateTime teacherConcludedAt,
        LocalDateTime executionStartedAt,
        LocalDateTime executionEndedAt,
        String plannedAction,
        TaskCriticality taskCriticality,
        MaintenanceType maintenanceType,
        String reportLink
) {
}