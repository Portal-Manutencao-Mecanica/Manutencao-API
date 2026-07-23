package com.weg.Maintenance_API.userimport.service;

public record SpreadsheetUserRow(
        int rowNumber,
        String name,
        String username,
        String email,
        String role,
        String organization
) {

    public boolean isEmpty() {
        return name.isBlank()
                && username.isBlank()
                && email.isBlank()
                && role.isBlank()
                && organization.isBlank();
    }
}
