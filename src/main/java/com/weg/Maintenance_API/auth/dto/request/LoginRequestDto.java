package com.weg.Maintenance_API.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Executa a operacao deste metodo.
public record LoginRequestDto(
        @NotBlank
        @Size(max = 150)
        @JsonAlias("email")
        String identifier,

        @NotBlank
        String password
) {
}
