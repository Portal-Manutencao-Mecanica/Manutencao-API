package com.weg.Maintenance_API.student.dto.request;

public record StudentPatchRequest(
        String name,
        String email,
        String password
) {
}