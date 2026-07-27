package com.weg.Maintenance_API.media.dto.response;


import java.util.UUID;

import com.weg.Maintenance_API.enums.MediaType;

import java.time.LocalDateTime;

// Executa a operacao deste metodo.
public record MediaResponseDto(
        UUID id,
        String description,
        MediaType mediaType,
        String image,
        String originalName,
        String contentType,
        Long fileSize,
        LocalDateTime createdAt
) {
}
