package com.weg.Maintenance_API.classgroup.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record ClassRequestDto(
        @NotBlank(message = "acronym can't be blank")
        String acronym,
        List<Long> teacherIds,
        List<Long> studentIds) {
}

