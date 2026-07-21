package com.weg.Maintenance_API.classgroup.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record ClassRequestDto(
        String acronym,
        List<Long> teacherIds,
        List<Long> studentIds) {
}

