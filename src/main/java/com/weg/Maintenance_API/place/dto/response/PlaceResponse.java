package com.weg.Maintenance_API.place.dto.response;


import java.util.UUID;

// Executa a operacao deste metodo.
public record PlaceResponse(
    UUID id,
    String name
) {
}
