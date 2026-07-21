package com.weg.Maintenance_API.coordinator.dto.request;

public record CoordinatorPatchRequest(
        String name,
        String email,
        String password
) {
}