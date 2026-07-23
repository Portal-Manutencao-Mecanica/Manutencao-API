package com.weg.Maintenance_API.auth.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class SecureTokenServiceTest {

    private final SecureTokenService secureTokenService = new SecureTokenService();

    @Test
    void generatesUnpredictableTokensAndStableSha256Hashes() {
        String first = secureTokenService.generate();
        String second = secureTokenService.generate();

        assertNotEquals(first, second);
        assertEquals(64, secureTokenService.hash(first).length());
        assertEquals(
                secureTokenService.hash(first),
                secureTokenService.hash(first)
        );
    }
}
