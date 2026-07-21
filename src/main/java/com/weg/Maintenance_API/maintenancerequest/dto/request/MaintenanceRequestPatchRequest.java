package com.weg.Maintenance_API.maintenancerequest.dto.request;

import com.weg.Maintenance_API.enums.Priority;
import com.weg.Maintenance_API.enums.Sector;

public record MaintenanceRequestPatchRequest(
        Sector sector,
        Priority priority,
        String description
) {
}