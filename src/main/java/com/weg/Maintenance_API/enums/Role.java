package com.weg.Maintenance_API.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Role {
    ADMIN("ADMIN"),
    ALUNO("ALUNO"),
    PROFESSOR("PROFESSOR"),
    COORDENADOR("COORDENADOR");

    private String role;
}

