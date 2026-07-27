package com.weg.Maintenance_API.machine.dto.request;


import java.util.UUID;

import com.weg.Maintenance_API.enums.EquipmentCondition;
import com.weg.Maintenance_API.place.entity.Place;

import com.weg.Maintenance_API.validation.enumValidator.ValidEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// Executa a operacao deste metodo.
public record MachineRequest(
    @NotBlank(message = "name can't be blank")
    String name,
    @NotBlank(message = "patrimony can't be blank")
    String patrimony,
    @NotNull(message = "condition can't be null")
    @ValidEnum(message = "condition is invalid",enumClass = EquipmentCondition.class)
    String condition,
    String tag,
    @NotNull(message = "place can't be null")
UUID placeId
) {
}
