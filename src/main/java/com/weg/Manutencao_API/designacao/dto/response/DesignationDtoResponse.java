package com.weg.Manutencao_API.designacao.dto.response;

import com.weg.Manutencao_API.enums.Sector;

public record DesignationDtoResponse(
        Long id,
        Sector sector
) {
}
