package com.weg.Maintenance_API.calendar.dto.response;

import com.weg.Maintenance_API.enums.MaintenanceType;
import com.weg.Maintenance_API.enums.TaskCriticality;
import com.weg.Maintenance_API.enums.TaskSituation;

import java.time.LocalDateTime;

public record CalendarResponseDto(
        Long id,
        String scheduledAction,
        TaskCriticality criticality,
        LocalDateTime createdAt,
        LocalDateTime scheduledFor,
        LocalDateTime requestedAt,
        Long studentId,
        String studentName,
        Long teacherId,
        String teacherName,
        Long equipmentId,
        String equipmentName,
        Integer machineId,
        String machineName,
        Long placeId,
        String placeName,
        MaintenanceType maintenanceType,
        TaskSituation status
) {
}
