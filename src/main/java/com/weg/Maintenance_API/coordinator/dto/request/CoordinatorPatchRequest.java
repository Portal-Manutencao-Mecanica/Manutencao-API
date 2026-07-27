package com.weg.Maintenance_API.coordinator.dto.request;

// Executa a operacao deste metodo.
public record CoordinatorPatchRequest(
        String name,
        String email,
        String password
) {
}
