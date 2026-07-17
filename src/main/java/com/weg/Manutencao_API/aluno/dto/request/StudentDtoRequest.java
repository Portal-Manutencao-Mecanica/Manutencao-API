package com.weg.Manutencao_API.aluno.dto.request;

import java.util.List;

import com.weg.Manutencao_API.turma.entity.ClassGroup;

public record StudentDtoRequest(
        String nome,
        String email,
        List<ClassGroup> classGroups) {

}
