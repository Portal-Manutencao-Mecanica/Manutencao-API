package com.weg.Maintenance_API.machine.dto.response;

import com.weg.Maintenance_API.enums.EquipmentCondition;

import java.time.LocalDateTime;

public record MachineResponse(
        Integer id,
        String name,
        String patrimony,
        EquipmentCondition condition,
        String tag,
        Long placeId,
        String placeName,
        LocalDateTime createdAt) {
}
