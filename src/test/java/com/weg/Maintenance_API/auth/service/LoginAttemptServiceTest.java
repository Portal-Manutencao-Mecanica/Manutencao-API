package com.weg.Maintenance_API.auth.service;

import com.weg.Maintenance_API.audit.service.AuditService;
import com.weg.Maintenance_API.student.entity.Student;
import com.weg.Maintenance_API.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginAttemptServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuditService auditService;

    private LoginAttemptService loginAttemptService;
    private Student user;

    @BeforeEach
    void setUp() {
        loginAttemptService = new LoginAttemptService(userRepository, auditService);
        user = new Student(
                "Aluno",
                "aluno@local.com",
                "hash",
                UUID.randomUUID().toString()
        );
        user.setId(UUID.randomUUID());
        user.setEnabled(true);
        user.setAccountNonLocked(true);
        when(userRepository.findByEmailForUpdate("aluno@local.com"))
                .thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void locksUserForFiveMinutesAfterFiveInvalidAttempts() {
        ClientRequestMetadata metadata =
                new ClientRequestMetadata("/auth/login", "POST", "127.0.0.1", "JUnit");

        for (int attempt = 0; attempt < 5; attempt++) {
            loginAttemptService.recordFailure("aluno@local.com", metadata);
        }

        assertEquals(0, user.getFailedLoginAttempts());
        assertEquals(1, user.getLockoutCount());
        assertNotNull(user.getLockedUntil());
    }
}
