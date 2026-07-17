package com.weg.Manutencao_API.materialapoio.dto.response;

public record HelperMaterialResponse(
        Long id,
        String linkTecnic,
        String linkLubrification,
        String linkPreventMaintance,
        String linkManual) {

}
