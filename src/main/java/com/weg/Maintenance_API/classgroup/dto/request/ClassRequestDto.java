package com.weg.Maintenance_API.classgroup.dto.request;


import java.util.UUID;

import java.util.List;

// Executa a operacao deste metodo.
public record ClassRequestDto(
        String acronym,
        List<UUID> teacherIds,
        List<UUID> studentIds) {
}
