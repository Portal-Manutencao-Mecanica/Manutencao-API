package com.weg.Manutencao_API.designacao.dto.request;

import com.weg.Manutencao_API.enums.Sector;
import jakarta.validation.constraints.NotNull;

public record DesignationDtoRequest(
        @NotNull(message = "sector can't be null")
        Sector sector
) {
}
