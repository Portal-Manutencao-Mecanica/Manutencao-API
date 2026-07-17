package com.weg.Manutencao_API.coordenador.dto.request;

import java.time.LocalDateTime;

import com.weg.Manutencao_API.enums.Role;

public record CoordinatorRequestDto(

        String nome,
        String email,
        Role role,
        LocalDateTime createdAt) {
}