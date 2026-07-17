package com.weg.Manutencao_API.aluno.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.weg.Manutencao_API.enums.Role;
import com.weg.Manutencao_API.turma.entity.ClassGroup;

public record StudentDtoResponse(
        Long id,
        String nome,
        String email,
        Role role,
        List<ClassGroup> classGroups,
        LocalDateTime createdAt) {

}
