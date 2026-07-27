package com.weg.Maintenance_API.user.controller;

import com.weg.Maintenance_API.auth.service.ClientRequestMetadata;
import com.weg.Maintenance_API.user.dto.request.CreateUserRequest;
import com.weg.Maintenance_API.user.dto.response.UserCreationResponse;
import com.weg.Maintenance_API.user.service.UserCreationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserManagementController {

    private final UserCreationService userCreationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    public UserCreationResponse create(
            @Valid @RequestBody CreateUserRequest request,
            Authentication authentication,
            HttpServletRequest httpRequest
    ) {
        return userCreationService.create(
                request,
                authentication.getName(),
                ClientRequestMetadata.from(httpRequest)
        );
    }
}
