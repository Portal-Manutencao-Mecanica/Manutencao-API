package com.weg.Manutencao_API.materialapoio.dto.request;

public record HelperMaterialRequest(
    String linkTecnic,
    String linkLubrification,
    String linkPreventMaintance,
    String linkManual
) {

}
