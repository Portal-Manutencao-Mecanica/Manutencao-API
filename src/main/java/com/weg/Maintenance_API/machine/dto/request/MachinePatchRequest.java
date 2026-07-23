package com.weg.Maintenance_API.machine.dto.request;

import com.weg.Maintenance_API.enums.EquipmentCondition;

public record MachinePatchRequest(
        String name,
        String patrimony,
        EquipmentCondition condition,
        String tag
) {
}