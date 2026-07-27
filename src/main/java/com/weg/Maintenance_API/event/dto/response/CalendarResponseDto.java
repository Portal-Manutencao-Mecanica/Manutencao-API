package com.weg.Maintenance_API.event.dto.response;


import java.util.UUID;

import com.weg.Maintenance_API.enums.MaintenanceType;
import com.weg.Maintenance_API.enums.TaskCriticality;
import com.weg.Maintenance_API.enums.TaskSituation;

import java.time.LocalDateTime;

public record CalendarResponseDto(
        UUID id,
        String numberCard,
        String scheduledAction,
        TaskCriticality criticality,
        LocalDateTime createdAt,
        LocalDateTime scheduledFor,
        LocalDateTime requestedAt,
        UUID studentId,
        String studentName,
        UUID teacherId,
        String teacherName,
        UUID equipmentId,
        String equipmentName,
        UUID machineId,
        String machineName,
        UUID placeId,
        String placeName,
        MaintenanceType maintenanceType,
        TaskSituation status
) {
}
