package com.weg.Manutencao_API.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Status {
    Entregue("ENTREGUE"),
    EmAnalise("EM-ANALISE"),
    Pedideemandamento("PEDIDO-EM-ANDAMENTO"),
    Naovisualizado("NAO-VISUALIZADO");

    private String status;
}
