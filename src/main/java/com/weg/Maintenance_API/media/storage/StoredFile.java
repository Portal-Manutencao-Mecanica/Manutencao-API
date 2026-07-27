package com.weg.Maintenance_API.media.storage;

public record StoredFile(
        String storageKey,
        String originalFilename,
        String contentType,
        long size
) {
}
