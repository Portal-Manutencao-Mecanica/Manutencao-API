package com.weg.Maintenance_API.validation.enumValidator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.Locale;

/**
 * Implements the validation rule defined by the ValidEnum annotation.
 */
public class EnumValidator implements ConstraintValidator<ValidEnum, String> {

    private Class<? extends Enum<?>> enumClass;

    // Executa a operacao deste metodo.
    @Override
    public void initialize(ValidEnum annotation) {
        this.enumClass = annotation.enumClass();
    }

    // Valida a regra aplicada por este metodo.
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Empty values are validated by @NotBlank when required.
        if (value == null || value.isBlank()) {
            return true;
        }

        String normalizedValue = value.trim().toUpperCase(Locale.ROOT);

        return Arrays.stream(enumClass.getEnumConstants())
                .map(Enum::name)
                .anyMatch(enumName -> enumName.equals(normalizedValue));
    }
}
