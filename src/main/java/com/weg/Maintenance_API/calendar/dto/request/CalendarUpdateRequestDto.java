package com.weg.Maintenance_API.calendar.dto.request;

import com.weg.Maintenance_API.enums.MaintenanceType;
import com.weg.Maintenance_API.enums.TaskCriticality;
import com.weg.Maintenance_API.enums.TaskSituation;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record CalendarUpdateRequestDto(
        String scheduledAction,
        TaskCriticality criticality,
        @FutureOrPresent LocalDateTime scheduledFor,
        @Positive Long studentId,
        @Positive Long teacherId,
        @Positive Long equipmentId,
        @Positive Integer machineId,
        @Positive Long placeId,
        MaintenanceType maintenanceType,
        TaskSituation status
) {
}
