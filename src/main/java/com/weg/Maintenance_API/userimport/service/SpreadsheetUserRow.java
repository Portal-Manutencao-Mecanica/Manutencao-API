package com.weg.Maintenance_API.userimport.service;

// Executa a operacao deste metodo.
public record SpreadsheetUserRow(
        int rowNumber,
        String name,
        String username,
        String email,
        String role,
        String organization
) {

    // Valida a regra aplicada por este metodo.
    public boolean isEmpty() {
        return name.isBlank()
                && username.isBlank()
                && email.isBlank()
                && role.isBlank()
                && organization.isBlank();
    }
}
