package com.weg.Manutencao_API.admin.dto.response;

import java.time.LocalDateTime;

import com.weg.Manutencao_API.enums.Role;

public record AdminResponseDto(
        Long id,
        String name,
        String email,
        Role role,
        LocalDateTime createdAt) {
}
