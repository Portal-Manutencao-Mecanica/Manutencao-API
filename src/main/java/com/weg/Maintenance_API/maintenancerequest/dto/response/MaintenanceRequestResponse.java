package com.weg.Maintenance_API.maintenancerequest.dto.response;


import java.util.UUID;

import com.weg.Maintenance_API.enums.MaintenanceRequestStatus;
import com.weg.Maintenance_API.enums.Priority;
import com.weg.Maintenance_API.enums.Sector;

import java.time.LocalDateTime;
import java.util.List;

public record MaintenanceRequestResponse(
        UUID id,
        String numberCard,
        MaintenanceRequestStatus status,
        Sector sector,
        Priority priority,
        List<UUID> assignedStudentIds,
        UUID placeId,
        String placeName,
        String description,
        LocalDateTime createdAt,
        UUID notifiedTeacherId,
        String notifiedTeacherName,
        UUID machineId,
        String machineName
) {
}
