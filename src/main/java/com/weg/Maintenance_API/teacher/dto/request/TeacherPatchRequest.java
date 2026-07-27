package com.weg.Maintenance_API.teacher.dto.request;

// Executa a operacao deste metodo.
public record TeacherPatchRequest(
        String name,
        String email,
        String password
) {
}
