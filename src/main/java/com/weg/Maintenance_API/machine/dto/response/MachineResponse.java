package com.weg.Maintenance_API.machine.dto.response;


import java.util.UUID;

import com.weg.Maintenance_API.enums.EquipmentCondition;

import java.time.LocalDateTime;

public record MachineResponse(
        UUID id,
        String numberCard,
        String name,
        String patrimony,
        EquipmentCondition condition,
        String tag,
        UUID placeId,
        String placeName,
        LocalDateTime createdAt) {
}
