package com.weg.Manutencao_API.local.dto.response;

import java.util.List;

public record PlaceResponse(
    Long id,
    String name,
    List<MachineResponse> machines
) {

}
