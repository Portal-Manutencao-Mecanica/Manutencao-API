package com.weg.Maintenance_API.inconvenience5s.dto.response;


import java.util.UUID;

import com.weg.Maintenance_API.enums.Inconvenience5SStatus;
import com.weg.Maintenance_API.enums.RegistrationPeriod;

import java.time.LocalDateTime;
import java.util.List;

// Executa a operacao deste metodo.
public record Inconvenience5SDtoResponse(
        UUID id,
        String inconvenience,
        Inconvenience5SStatus status,
        UUID placeId,
        String placeName,
        UUID notifiedTeacherId,
        String notifiedTeacherName,
        LocalDateTime createdAt,
        UUID classGroupId,
        String classGroupAcronym,
        List<UUID> involvedStudentIds,
        String description,
        RegistrationPeriod registrationPeriod
) {
}
