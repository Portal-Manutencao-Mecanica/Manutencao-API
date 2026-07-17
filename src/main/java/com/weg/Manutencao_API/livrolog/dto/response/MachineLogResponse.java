package com.weg.Manutencao_API.livrolog.dto.response;

import java.util.List;

import com.weg.Manutencao_API.enums.MaintenanceType;
import com.weg.Manutencao_API.enums.TaskCriticality;
import com.weg.Manutencao_API.enums.TaskSituation;
import com.weg.Manutencao_API.local.dto.response.PlaceResponse;
import com.weg.Manutencao_API.maquina.dto.response.MachineResponse;

public record MachineLogResponse(
        Long id,
        String title,
        String description,
        String content,
        TaskSituation taskSituation,
        MachineResponse machine,
        String serviceExecute,
        TeacherResponse teacher,
        String actionToExecute,
        TaskCriticality taskCriticality,
        byte[] image,
        PlaceResponse place,
        MaintenanceType maintenanceType,
        String registrationPeriod,
        ClassGroupResponse classGroupId,
        List<StudentResponse> assignedStudentsId,
        String reportLink) {

}
