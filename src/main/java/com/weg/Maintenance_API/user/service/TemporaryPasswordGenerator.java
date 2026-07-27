package com.weg.Maintenance_API.user.service;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class TemporaryPasswordGenerator {

    private static final char[] UPPER = "ABCDEFGHJKLMNPQRSTUVWXYZ".toCharArray();
    private static final char[] LOWER = "abcdefghijkmnopqrstuvwxyz".toCharArray();
    private static final char[] DIGITS = "23456789".toCharArray();
    private static final char[] SPECIAL = "!@#$%&*?".toCharArray();
    private static final char[] ALL =
            "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789!@#$%&*?"
                    .toCharArray();

    private final SecureRandom secureRandom = new SecureRandom();

    public String generate() {
        char[] password = new char[16];
        password[0] = random(UPPER);
        password[1] = random(LOWER);
        password[2] = random(DIGITS);
        password[3] = random(SPECIAL);
        for (int index = 4; index < password.length; index++) {
            password[index] = random(ALL);
        }
        shuffle(password);
        return new String(password);
    }

    private char random(char[] characters) {
        return characters[secureRandom.nextInt(characters.length)];
    }

    private void shuffle(char[] characters) {
        for (int index = characters.length - 1; index > 0; index--) {
            int selected = secureRandom.nextInt(index + 1);
            char current = characters[index];
            characters[index] = characters[selected];
            characters[selected] = current;
        }
    }
}
