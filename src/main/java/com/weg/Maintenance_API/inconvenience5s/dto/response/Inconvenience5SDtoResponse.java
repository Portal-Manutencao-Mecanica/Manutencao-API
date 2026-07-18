package com.weg.Maintenance_API.inconvenience5s.dto.response;

import com.weg.Maintenance_API.enums.Inconvenience5SStatus;
import com.weg.Maintenance_API.enums.RegistrationPeriod;

import java.time.LocalDateTime;
import java.util.List;

public record Inconvenience5SDtoResponse(
        Long id,
        String inconvenience,
        Inconvenience5SStatus status,
        Long placeId,
        String placeName,
        Long notifiedTeacherId,
        String notifiedTeacherName,
        LocalDateTime createdAt,
        Long classGroupId,
        String classGroupAcronym,
        List<Long> involvedStudentIds,
        String description,
        RegistrationPeriod registrationPeriod
) {
}
