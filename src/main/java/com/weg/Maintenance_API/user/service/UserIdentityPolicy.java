package com.weg.Maintenance_API.user.service;

import com.weg.Maintenance_API.exception.type.ConflictException;
import com.weg.Maintenance_API.exception.type.InvalidRequestException;
import com.weg.Maintenance_API.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserIdentityPolicy {

    private static final Set<String> RESERVED_USERNAMES =
            Set.of("admin", "root", "system", "support", "administrator");
    private static final Pattern USERNAME_PATTERN =
            Pattern.compile("^[a-z0-9][a-z0-9._-]{2,49}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$",
            Pattern.CASE_INSENSITIVE
    );

    private final UserRepository userRepository;

    // Converte os dados para o formato necessario.
    public String normalizeUsername(String username) {
        if (username == null) {
            return "";
        }
        String withoutAccents = Normalizer.normalize(username, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return withoutAccents
                .trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", ".");
    }

    // Converte os dados para o formato necessario.
    public String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }

    // Valida a regra aplicada por este metodo.
    public void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new InvalidRequestException("O nome Ã© obrigatÃ³rio.");
        }
        if (name.trim().length() > 150) {
            throw new InvalidRequestException("O nome deve possuir no mÃ¡ximo 150 caracteres.");
        }
    }

    // Valida a regra aplicada por este metodo.
    public void validateUsername(String username) {
        if (RESERVED_USERNAMES.contains(username)) {
            throw new InvalidRequestException("O username informado Ã© reservado.");
        }
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            throw new InvalidRequestException(
                    "O username deve comeÃ§ar com letra ou nÃºmero e usar apenas letras minÃºsculas, nÃºmeros, ponto, hÃ­fen ou sublinhado."
            );
        }
    }

    // Valida a regra aplicada por este metodo.
    public void validateEmail(String email) {
        if (email.isBlank() || email.length() > 150 || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new InvalidRequestException("O e-mail informado Ã© invÃ¡lido.");
        }
    }

    // Valida a regra aplicada por este metodo.
    public void validateAvailable(String username, String email) {
        if (userRepository.existsByUsernameIgnoreCase(username)) {
            throw new ConflictException("O username informado jÃ¡ estÃ¡ cadastrado.");
        }
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ConflictException("O e-mail informado jÃ¡ estÃ¡ cadastrado.");
        }
    }    // Valida a disponibilidade do e-mail informado.
    public void validateEmailAvailable(String email) {
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ConflictException("O e-mail informado já está cadastrado.");
        }
    }
}
