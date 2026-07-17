package com.weg.Manutencao_API.solicitacaomanutencao.dto.response;

import java.util.List;

import com.weg.Manutencao_API.aluno.entity.Student;
import com.weg.Manutencao_API.enums.MaintenanceRequestStatus;
import com.weg.Manutencao_API.enums.Priority;
import com.weg.Manutencao_API.local.dto.response.PlaceResponse;
import com.weg.Manutencao_API.professor.entity.Teacher;

public record MaintanceRequestResponse(
        Long id,
        MaintenanceRequestStatus status,
        DesignationResponse designation,
        Priority priority,
        List<StudentResponse> students,
        PlaceResponse placeResponse,
        String description,
        List<byte[]> anomalyImages,
        String patrymony,
        String tag,
        TeacherResponse teacher,
        MachineResponse MachineId) {

}
