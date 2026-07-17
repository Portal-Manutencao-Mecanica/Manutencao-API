package com.weg.Manutencao_API.admin.dto.response;

import java.time.LocalDateTime;

import javax.management.relation.Role;

public record AdminResponseDto(
        String name,
        String email,
        Role role,
        LocalDateTime createdAt) {
}
