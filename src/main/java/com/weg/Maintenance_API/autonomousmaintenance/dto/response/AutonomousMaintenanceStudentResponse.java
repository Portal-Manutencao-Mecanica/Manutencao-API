package com.weg.Maintenance_API.autonomousmaintenance.dto.response;

import java.util.UUID;

public record AutonomousMaintenanceStudentResponse(
        UUID id,
        String name,
        String email,
        String numberCard
) {
}
