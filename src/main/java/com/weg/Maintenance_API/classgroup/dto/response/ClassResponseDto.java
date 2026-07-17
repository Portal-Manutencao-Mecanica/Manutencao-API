package com.weg.Maintenance_API.classgroup.dto.response;

import java.util.List;

public record ClassResponseDto(
        Long id,
        String acronym,
        List<Long> teacherIds,
        List<Long> studentIds) {
}

