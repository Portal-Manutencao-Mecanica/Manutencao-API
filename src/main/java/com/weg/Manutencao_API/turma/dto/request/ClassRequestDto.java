package com.weg.Manutencao_API.turma.dto.request;

import java.time.LocalDateTime;
import java.util.List;

import com.weg.Manutencao_API.aluno.entity.Student;
import com.weg.Manutencao_API.enums.Role;
import com.weg.Manutencao_API.professor.entity.Teacher;

public record ClassRequestDto(
        String nome,
        String email,
        String acronym,
        Role role,
        LocalDateTime createdAt,
        List<Teacher> teachers,
        List<Student> students) {
}