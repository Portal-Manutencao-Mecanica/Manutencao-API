package com.weg.Maintenance_API.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidPasswordValidator implements ConstraintValidator<ValidPassword, String> {

    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 72;
    private static final String SPECIAL_CHARACTERS = "!@#$%^&*()_+-=[]{};':\\\"|,.<>/?";

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            return true;
        }

        if (password.length() < MIN_LENGTH || password.length() > MAX_LENGTH) {
            return false;
        }

        boolean hasUppercase = false;
        boolean hasNumber = false;
        boolean hasSpecial = false;

        for (int index = 0; index < password.length(); index++) {
            char character = password.charAt(index);
            hasUppercase |= Character.isUpperCase(character);
            hasNumber |= Character.isDigit(character);
            hasSpecial |= SPECIAL_CHARACTERS.indexOf(character) >= 0;
        }

        return hasUppercase && hasNumber && hasSpecial;
    }
}
