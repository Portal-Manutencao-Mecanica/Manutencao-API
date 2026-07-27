package com.weg.Maintenance_API.event.dto.request;


import java.util.UUID;

import com.weg.Maintenance_API.enums.MaintenanceType;
import com.weg.Maintenance_API.enums.TaskCriticality;
import com.weg.Maintenance_API.enums.TaskSituation;
import com.weg.Maintenance_API.validation.enumValidator.ValidEnum;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDateTime;

// Executa a operacao deste metodo.
public record CalendarCreateRequestDto(
        @NotBlank(message = "A acao agendada e obrigatoria.")
        String scheduledAction,
        @NotNull(message = "A criticidade e obrigatoria.")
        @ValidEnum(message = "A criticidade informada e invalida.", enumClass = TaskCriticality.class)
        String criticality,
        @NotNull(message = "A data agendada e obrigatoria.")
        @FutureOrPresent(message = "A data agendada nao pode estar no passado.")
        LocalDateTime scheduledFor,
        @NotNull(message = "A data da solicitacao e obrigatoria.")
        @PastOrPresent(message = "A data da solicitacao nao pode estar no futuro.")
        LocalDateTime requestedAt,
UUID studentId,
        @NotNull(message = "O professor e obrigatorio.")
        UUID teacherId,
        @NotNull(message = "O equipamento e obrigatorio.")
        UUID equipmentId,
        @NotNull(message = "A maquina e obrigatoria.")
        UUID machineId,
        @NotNull(message = "O local e obrigatorio.")
        UUID placeId,
        @NotNull(message = "O tipo de manutencao e obrigatorio.")
        MaintenanceType maintenanceType,
        TaskSituation status
) {
}
