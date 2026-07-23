package com.weg.Maintenance_API.classgroup.dto.response;

import com.weg.Maintenance_API.student.dto.response.StudentDtoResponse;
import com.weg.Maintenance_API.teacher.dto.response.TeacherResponseDto;

import java.util.List;

public record ClassResponseDto(
        Long id,
        String numberCard,
        String acronym,
        List<TeacherResponseDto> teachers,
        List<StudentDtoResponse> students) {
}
