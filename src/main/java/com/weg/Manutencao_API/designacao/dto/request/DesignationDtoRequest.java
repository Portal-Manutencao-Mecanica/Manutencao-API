package com.weg.Manutencao_API.designacao.dto.request;

import jakarta.validation.constraints.NotNull;

public record DesignationDtoRequest(
        @NotNull(message = "sector can't be null")
        String sector
) {
}
