package com.weg.Manutencao_API.admin.dto.response;

import java.time.LocalDateTime;

import com.weg.Manutencao_API.enums.Role;

public record AdminResponseDto(
        String name,
        String email,
        Role role,
        LocalDateTime createdAt) {
}
