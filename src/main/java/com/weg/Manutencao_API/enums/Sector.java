package com.weg.Manutencao_API.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Sector {
    AREA_NAO_DESIGNADA("AREA_NAO_DESIGNADA"),
    CENTRO_WEG("CENTRO_WEG"),
    WEG_MANUTENCAO("WEG_MANUTENCAO");

    private String sector;
}
