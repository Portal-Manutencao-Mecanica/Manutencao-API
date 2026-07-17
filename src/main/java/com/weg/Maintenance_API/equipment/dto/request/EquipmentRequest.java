package com.weg.Maintenance_API.equipment.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record EquipmentRequest(
    @NotBlank(message = "name can't be blank")
    String name,
    String sap,
    @NotNull(message = "price can't be null")
    @DecimalMin(value = "0.0", inclusive = true, message = "price can't be negative")
    BigDecimal price,
    @PositiveOrZero(message = "quantity can't be negative")
    int quantity
) {
}


