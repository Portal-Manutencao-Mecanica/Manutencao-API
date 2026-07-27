package com.weg.Maintenance_API.buy.dto.response;


import java.util.UUID;

// Executa a operacao deste metodo.
public record StudentDtoResponse(
        UUID id,
        String name,
        String email
) {
}
