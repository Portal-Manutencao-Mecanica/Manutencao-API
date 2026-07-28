package com.weg.Maintenance_API.event.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;

public record EventResponseDto(
    LocalDate day,
    LocalTime hour,
    String title
) {
}
