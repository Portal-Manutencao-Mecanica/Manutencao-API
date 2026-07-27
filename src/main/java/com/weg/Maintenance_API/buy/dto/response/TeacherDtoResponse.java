package com.weg.Maintenance_API.buy.dto.response;


import java.util.UUID;

public record TeacherDtoResponse(
        UUID id,
        String numberCard,
        String name,
        String email
) {
}


