package com.weg.Maintenance_API.buy.dto.response;


import java.util.UUID;

public record ClassGroupDtoResponse(
        UUID id,
        String numberCard,
        String acronym
) {
}


