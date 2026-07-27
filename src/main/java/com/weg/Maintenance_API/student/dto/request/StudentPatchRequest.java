package com.weg.Maintenance_API.student.dto.request;

// Executa a operacao deste metodo.
public record StudentPatchRequest(
        String name,
        String email,
        String password
) {
}
