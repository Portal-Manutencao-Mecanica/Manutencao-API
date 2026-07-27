package com.weg.Maintenance_API.event.dto.request;


import java.util.UUID;

import com.weg.Maintenance_API.enums.MaintenanceType;
import com.weg.Maintenance_API.enums.TaskCriticality;
import com.weg.Maintenance_API.enums.TaskSituation;
import com.weg.Maintenance_API.validation.enumValidator.ValidEnum;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDateTime;

// Executa a operacao deste metodo.
public record CalendarUpdateRequestDto(
        String scheduledAction,
        @ValidEnum(message = "A criticidade informada e invalida.", enumClass = TaskCriticality.class)
        String criticality,
        @FutureOrPresent(message = "A data agendada nao pode estar no passado.") LocalDateTime scheduledFor,
        @PastOrPresent(message = "A data da solicitacao nao pode estar no futuro.") LocalDateTime requestedAt,
UUID studentId,
UUID teacherId,
UUID equipmentId,
UUID machineId,
UUID placeId,
        @ValidEnum(message = "O tipo de manutencao informado e invalido.", enumClass = MaintenanceType.class)
        String maintenanceType,
        @ValidEnum(message = "A situacao informada e invalida.", enumClass = TaskSituation.class)
        String status
) {
}
