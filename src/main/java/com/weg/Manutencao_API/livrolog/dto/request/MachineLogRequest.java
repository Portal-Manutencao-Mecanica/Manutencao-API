package com.weg.Manutencao_API.livrolog.dto.request;

import java.time.LocalDateTime;
import java.util.List;

import com.weg.Manutencao_API.enums.MaintenanceType;
import com.weg.Manutencao_API.enums.TaskCriticality;
import com.weg.Manutencao_API.enums.TaskSituation;

public record MachineLogRequest(
        String title,
        String description,
        String content,
        TaskSituation taskSituation,
        Long machineId,
        String serviceExecute,
        Long teacheId,
        String actionToExecute,
        TaskCriticality taskCriticality,
        byte[] image,
        Long placeId,
        MaintenanceType maintenanceType,
        String registrationPeriod,
        Long classGroupId,
        List<Long> assignedStudentsId,
        String reportLink) {

}
