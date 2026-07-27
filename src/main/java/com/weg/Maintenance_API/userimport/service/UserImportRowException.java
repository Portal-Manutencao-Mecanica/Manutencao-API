package com.weg.Maintenance_API.userimport.service;

import com.weg.Maintenance_API.enums.Role;

public class UserImportRowException extends RuntimeException {

    private final String code;
    private final String field;
    private final Role role;

    public UserImportRowException(
            String code,
            String field,
            String message,
            Role role
    ) {
        super(message);
        this.code = code;
        this.field = field;
        this.role = role;
    }

    // Executa a operacao deste metodo.
    public String code() {
        return code;
    }

    // Executa a operacao deste metodo.
    public String field() {
        return field;
    }

    // Executa a operacao deste metodo.
    public Role role() {
        return role;
    }
}
