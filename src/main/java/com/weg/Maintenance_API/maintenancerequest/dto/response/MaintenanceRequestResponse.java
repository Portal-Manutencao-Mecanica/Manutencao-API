package com.weg.Maintenance_API.maintenancerequest.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.weg.Maintenance_API.enums.MaintenanceRequestStatus;
import com.weg.Maintenance_API.enums.Priority;
import com.weg.Maintenance_API.enums.Sector;
import com.weg.Maintenance_API.media.dto.response.MediaResponseDto;

public record MaintenanceRequestResponse(
        UUID id,
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
        String machineName,
        UUID approvedById,
        String approvedByName,
        LocalDateTime approvedAt,
        String rejectionReason,
        String workOrderNumber,
        LocalDateTime workOrderCreatedAt,
        UUID workOrderCreatedById,
        String workOrderCreatedByName,
        UUID coordinatorApprovedById,
        String coordinatorApprovedByName,
        LocalDateTime coordinatorApprovedAt,
        String coordinatorRejectionReason,
        List<MediaResponseDto> media
) {
}
