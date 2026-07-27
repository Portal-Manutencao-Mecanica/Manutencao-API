package com.weg.Maintenance_API.machinelog.dto.request;

import com.weg.Maintenance_API.enums.MaintenanceType;
import com.weg.Maintenance_API.enums.TaskCriticality;
import com.weg.Maintenance_API.enums.TaskSituation;
import com.weg.Maintenance_API.validation.enumValidator.ValidEnum;

import java.time.LocalDateTime;

// Executa a operacao deste metodo.
public record MachineLogPatchRequest(
        String title,
        String description,
        String executionReport,
        @ValidEnum(message = "A situacao informada e invalida.", enumClass = TaskSituation.class)
        String taskSituation,
        String servicePerformed,
        LocalDateTime teacherConcludedAt,
        LocalDateTime executionStartedAt,
        LocalDateTime executionEndedAt,
        String plannedAction,
        @ValidEnum(message = "A criticidade informada e invalida.", enumClass = TaskCriticality.class)
        String taskCriticality,
        @ValidEnum(message = "O tipo de manutencao informado e invalido.", enumClass = MaintenanceType.class)
        String maintenanceType,
        String reportLink
) {
}
