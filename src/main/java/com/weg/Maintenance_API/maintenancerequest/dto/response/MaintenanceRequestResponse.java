package com.weg.Maintenance_API.maintenancerequest.dto.response;

import com.weg.Maintenance_API.enums.MaintenanceRequestStatus;
import com.weg.Maintenance_API.enums.Priority;

import java.time.LocalDateTime;
import java.util.List;

public record MaintenanceRequestResponse(
        Integer id,
        MaintenanceRequestStatus status,
        Long designationId,
        String designationSector,
        Priority priority,
        List<Long> studentIds,
        Long placeId,
        String placeName,
        String description,
        List<byte[]> anomalyImages,
        String patrimony,
        String tag,
        LocalDateTime dateTime,
        Long teacherId,
        String teacherName,
        Integer machineId,
        String machineName) {
}


