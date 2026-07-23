package com.weg.Maintenance_API.teacher.dto.request;

public record TeacherPatchRequest(
        String name,
        String email,
        String password
) {
}