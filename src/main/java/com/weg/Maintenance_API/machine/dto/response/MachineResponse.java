package com.weg.Maintenance_API.machine.dto.response;


import java.util.UUID;

import com.weg.Maintenance_API.enums.EquipmentCondition;

import java.time.LocalDateTime;

// Executa a operacao deste metodo.
public record MachineResponse(
        UUID id,
        String name,
        String patrimony,
        EquipmentCondition condition,
        String tag,
        UUID placeId,
        String placeName,
        String image,
        LocalDateTime createdAt) {
}
