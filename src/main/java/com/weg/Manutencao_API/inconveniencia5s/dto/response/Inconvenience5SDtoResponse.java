package com.weg.Manutencao_API.inconveniencia5s.dto.response;

import java.time.LocalDate;
import java.util.List;

public record Inconvenience5SDtoResponse(
        Integer id,
        String inconvenience,
        Boolean status,
        Long placeId,
        String placeName,
        Long teacherId,
        String teacherName,
        LocalDate dateTime,
        Long classGroupId,
        String classGroupAcronym,
        List<Long> studentIds,
        String description,
        List<String> images,
        String registeredOccasion
) {
}
