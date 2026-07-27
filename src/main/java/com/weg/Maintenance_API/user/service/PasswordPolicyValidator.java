package com.weg.Maintenance_API.user.service;

import com.weg.Maintenance_API.exception.type.InvalidRequestException;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.regex.Pattern;

@Component
public class PasswordPolicyValidator {

    private static final Pattern UPPERCASE = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE = Pattern.compile("[a-z]");
    private static final Pattern NUMBER = Pattern.compile("\\d");
    private static final Pattern SPECIAL = Pattern.compile("[^A-Za-z0-9]");

    public void validate(String password, String confirmation) {
        if (!Objects.equals(password, confirmation)) {
            throw new InvalidRequestException("A nova senha e a confirmação não coincidem.");
        }
        if (password == null || password.length() < 8 || password.length() > 128) {
            throw new InvalidRequestException(
                    "A senha deve possuir entre 8 e 128 caracteres."
            );
        }
        if (!UPPERCASE.matcher(password).find()
                || !LOWERCASE.matcher(password).find()
                || !NUMBER.matcher(password).find()
                || !SPECIAL.matcher(password).find()) {
            throw new InvalidRequestException(
                    "A senha deve conter letra maiúscula, letra minúscula, número e caractere especial."
            );
        }
    }
}
