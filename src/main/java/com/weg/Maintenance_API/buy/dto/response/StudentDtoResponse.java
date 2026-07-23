package com.weg.Maintenance_API.buy.dto.response;


import java.util.UUID;

public record StudentDtoResponse(
        UUID id,
        String numberCard,
        String name,
        String email
) {
}


