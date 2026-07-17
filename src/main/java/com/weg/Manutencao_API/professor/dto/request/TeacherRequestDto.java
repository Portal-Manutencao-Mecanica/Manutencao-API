package com.weg.Manutencao_API.professor.dto.request;

import java.time.LocalDateTime;
import java.util.List;

import com.weg.Manutencao_API.enums.Role;
import com.weg.Manutencao_API.turma.entity.ClassGroup;

public record TeacherRequestDto(
        String nome,
        String email,
        Role role,
        LocalDateTime createdAt,
        List<ClassGroup> classGroups) {
}
