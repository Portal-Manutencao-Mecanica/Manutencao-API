package com.weg.Maintenance_API.machine.dto.request;


import java.util.UUID;

import com.weg.Maintenance_API.enums.EquipmentCondition;
import com.weg.Maintenance_API.place.entity.Place;
import com.weg.Maintenance_API.validation.EntityExists;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MachineRequest(
    @NotBlank(message = "name can't be blank")
    String name,
    @NotBlank(message = "patrimony can't be blank")
    String patrimony,
    @NotNull(message = "condition can't be null")
    EquipmentCondition condition,
    String tag,
    @NotNull(message = "place can't be null")
    @EntityExists(entityClass = Place.class, message = "place not found")
    UUID placeId
) {
}


