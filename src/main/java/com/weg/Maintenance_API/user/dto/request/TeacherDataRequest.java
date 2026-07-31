package com.weg.Maintenance_API.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

@Schema(description = "Dados especificos do perfil de professor.")
public record TeacherDataRequest(
        @Schema(description = "UUIDs das turmas do professor.", example = "[\"00000000-0000-4000-8000-000000000010\"]")
        @NotEmpty(message = "Informe ao menos uma turma para o professor.")
        List<@NotNull(message = "O id da turma nao pode ser nulo.") UUID> classGroupIds
) {
}
