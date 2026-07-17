package com.weg.Manutencao_API.coordenador.dto.response;

import java.time.LocalDateTime;

import com.weg.Manutencao_API.enums.Role;

public record CoordinatorResponseDto(

        Long id,
        String nome,
        String email,
        Role role,
        LocalDateTime createdAt

) {
}
