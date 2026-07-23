package com.weg.Maintenance_API.user.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TemporaryPasswordGeneratorTest {

    @Test
    void generatedPasswordMatchesRequiredPolicy() {
        String password = new TemporaryPasswordGenerator().generate();

        assertTrue(password.length() >= 8);
        assertTrue(password.matches(".*[A-Z].*"));
        assertTrue(password.matches(".*[a-z].*"));
        assertTrue(password.matches(".*\\d.*"));
        assertTrue(password.matches(".*[!@#$%&*?].*"));
    }
}
