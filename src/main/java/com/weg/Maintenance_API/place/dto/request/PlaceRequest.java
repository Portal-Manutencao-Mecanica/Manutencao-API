package com.weg.Maintenance_API.place.dto.request;

import jakarta.validation.constraints.NotBlank;

// Executa a operacao deste metodo.
public record PlaceRequest(
    @NotBlank(message = "name can't be blank")
    String name
) {
}
