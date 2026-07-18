package com.weg.Maintenance_API.media.dto.response;

import com.weg.Maintenance_API.enums.MediaType;

import java.time.LocalDateTime;

public record MediaResponseDto(
        Long id,
        String description,
        MediaType mediaType,
        String storageKey,
        String originalName,
        String contentType,
        Long fileSize,
        LocalDateTime createdAt
) {
}
