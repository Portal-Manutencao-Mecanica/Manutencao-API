package com.weg.Manutencao_API.maquina.dto.request;

import java.util.List;

import com.weg.Manutencao_API.enums.EquipmentCondition;

public record MachineRequest(
    String name,
    String patrimony,
    EquipmentCondition condition,
    String tag,
    Long placeId,
    List<Long> machineLogs
) {

}
