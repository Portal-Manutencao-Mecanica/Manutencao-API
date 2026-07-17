package com.weg.Manutencao_API.solicitacaomanutencao.dto.request;

import java.util.List;

import com.weg.Manutencao_API.designacao.entity.Designation;
import com.weg.Manutencao_API.enums.MaintenanceRequestStatus;
import com.weg.Manutencao_API.enums.Priority;
import com.weg.Manutencao_API.local.dto.response.PlaceResponse;

public record MaintanceRequestRequest(

    MaintenanceRequestStatus status,
    Long designationId,
    Priority priority,
    List<Long> students,
    Long placeId,
    String description,
    List<byte[]> anomalyImages,
    String patrymony,
    String tag,
    Long teacherId,
    Long MachineId
) {

}
