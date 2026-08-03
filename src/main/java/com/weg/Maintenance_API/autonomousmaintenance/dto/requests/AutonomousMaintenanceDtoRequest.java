package com.weg.Maintenance_API.autonomousmaintenance.dto.requests;

import com.weg.Maintenance_API.enums.EquipmentCondition;
import com.weg.Maintenance_API.enums.EquipmentSituation;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record AutonomousMaintenanceDtoRequest(
        @NotNull(message = "A situacao do equipamento e obrigatoria.")
        EquipmentSituation equipmentSituation,
        @NotNull(message = "A data planejada e obrigatoria.")
        @FutureOrPresent(message = "A data planejada nao pode estar no passado.")
        LocalDateTime scheduledFor,
        @PastOrPresent(message = "A data da inspecao nao pode estar no futuro.")
        LocalDateTime inspectedAt,
        @NotNull(message = "A maquina inspecionada e obrigatoria.")
        UUID inspectedMachineId,
        @NotNull(message = "A condicao do equipamento e obrigatoria.")
        EquipmentCondition equipmentCondition,
        @Size(max = 5000, message = "As nao conformidades devem possuir no maximo 5000 caracteres.")
        String identifiedNonconformities,
        @NotEmpty(message = "Informe pelo menos um aluno.")
        List<@NotNull(message = "O identificador do aluno nao pode ser nulo.") UUID> studentIds
) {
}
