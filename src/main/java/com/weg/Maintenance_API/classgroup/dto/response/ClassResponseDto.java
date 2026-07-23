package com.weg.Maintenance_API.classgroup.dto.response;


import java.util.UUID;

import com.weg.Maintenance_API.student.dto.response.StudentDtoResponse;
import com.weg.Maintenance_API.teacher.dto.response.TeacherResponseDto;

import java.util.List;

public record ClassResponseDto(
        UUID id,
        String numberCard,
        String acronym,
        boolean enabled,
        List<TeacherResponseDto> teachers,
        List<StudentDtoResponse> students) {
}
