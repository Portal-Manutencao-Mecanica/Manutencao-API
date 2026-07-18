package com.weg.Maintenance_API.maintenancerequest.dto.response;

import com.weg.Maintenance_API.enums.MaintenanceRequestStatus;
import com.weg.Maintenance_API.enums.Priority;
import com.weg.Maintenance_API.enums.Sector;

import java.time.LocalDateTime;
import java.util.List;

public record MaintenanceRequestResponse(
        Long id,
        MaintenanceRequestStatus status,
        Sector sector,
        Priority priority,
        List<Long> assignedStudentIds,
        Long placeId,
        String placeName,
        String description,
        LocalDateTime createdAt,
        Long notifiedTeacherId,
        String notifiedTeacherName,
        Long machineId,
        String machineName
) {
}
