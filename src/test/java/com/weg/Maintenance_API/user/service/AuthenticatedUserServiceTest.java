package com.weg.Maintenance_API.user.service;

import com.weg.Maintenance_API.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class AuthenticatedUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void rejectsMissingAuthenticationWithoutNullPointerException() {
        AuthenticatedUserService service = new AuthenticatedUserService(userRepository);

        assertThrows(
                AuthenticationCredentialsNotFoundException.class,
                service::requireCurrentUser
        );
    }
}
