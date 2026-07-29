package com.weg.Maintenance_API.maintenancerequest.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MaintenanceApprovalRequest(
        @NotNull(message = "approved is required")
        Boolean approved,
        @Size(max = 1000, message = "reason must have at most 1000 characters")
        String reason
) {
}