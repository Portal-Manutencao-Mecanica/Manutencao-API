package com.weg.Maintenance_API.autonomousmaintenance.dto.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AutonomousMaintenanceApprovalRequest(
        @NotNull(message = "A decisao e obrigatoria.")
        Boolean approved,
        @Size(max = 2000, message = "O motivo deve possuir no maximo 2000 caracteres.")
        String reason
) {
}
