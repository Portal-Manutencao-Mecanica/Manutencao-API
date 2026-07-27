package com.weg.Maintenance_API.user.service;

import com.weg.Maintenance_API.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TemporaryCredentialService {

    private final TemporaryPasswordGenerator temporaryPasswordGenerator;
    private final PasswordEncoder passwordEncoder;

    public String issue(User user) {
        String temporaryPassword = temporaryPasswordGenerator.generate();
        user.setPassword(passwordEncoder.encode(temporaryPassword));
        user.setPasswordChangeRequired(true);
        user.setTemporaryPasswordExpiresAt(LocalDateTime.now().plusDays(3));
        return temporaryPassword;
    }
}
