package com.weg.Maintenance_API.classgroup.dto.request;


import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record ClassRequestDto(
        String acronym,
        List<UUID> teacherIds,
        List<UUID> studentIds) {
}

