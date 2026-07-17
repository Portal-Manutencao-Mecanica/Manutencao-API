package com.weg.Manutencao_API.local.dto;

import java.util.List;

public record PlaceResponse(
    Long id,
    String name,
    List<MachineResponse> machines
) {

}
