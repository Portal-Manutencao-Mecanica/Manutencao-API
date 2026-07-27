package com.weg.Maintenance_API.place.dto.response;


import java.util.UUID;

public record PlaceResponse(
    UUID id,
        String numberCard,
    String name
) {
}


