package com.weg.Manutencao_API.local.dto.request;

import java.util.List;

public record PlaceRequest(
    String name,
    List<Long> machineId
) {

}
