package com.weg.Maintenance_API.user.dto.response;

import java.util.UUID;

public record ProfilePhotoResponse(
        UUID id,
        String originalFilename,
        String contentType,
        long size
) {
}
