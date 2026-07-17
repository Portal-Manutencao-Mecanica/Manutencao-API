package com.weg.Manutencao_API.maquina.dto.response;

import java.util.List;

import com.weg.Manutencao_API.enums.EquipmentCondition;
import com.weg.Manutencao_API.local.dto.response.PlaceResponse;
import com.weg.Manutencao_API.local.entity.Place;

public record MachineResponse(
        Long id,
        String name,
        String patrimony,
        EquipmentCondition condition,
        String tag,
        PlaceResponse place,
        List<MachineResponse> machineLogs) {

}
