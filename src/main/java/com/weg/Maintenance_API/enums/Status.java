package com.weg.Maintenance_API.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Status {
    ENTREGUE("ENTREGUE"),
    EM_ANALISE("EM_ANALISE"),
    PEDIDO_EM_ANDAMENTO("PEDIDO_EM_ANDAMENTO"),
    NAO_VISUALIZADO("NAO_VISUALIZADO");

    private String status;
}

