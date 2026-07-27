package com.weg.Maintenance_API.user.controller;

import com.weg.Maintenance_API.auth.dto.request.LoginRequestDto;
import com.weg.Maintenance_API.auth.dto.request.LogoutRequest;
import com.weg.Maintenance_API.auth.dto.request.RefreshTokenRequest;
import com.weg.Maintenance_API.auth.dto.response.LoginResponseDto;
import com.weg.Maintenance_API.auth.service.AuthService;
import com.weg.Maintenance_API.auth.service.ClientRequestMetadata;
import com.weg.Maintenance_API.user.dto.response.UserResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class UserController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(
            @Valid @RequestBody LoginRequestDto request,
            HttpServletRequest httpRequest
    ) {
        return ResponseEntity.ok(
                authService.login(request, ClientRequestMetadata.from(httpRequest))
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDto> refresh(
            @Valid @RequestBody RefreshTokenRequest request,
            HttpServletRequest httpRequest
    ) {
        return ResponseEntity.ok(
                authService.refresh(
                        request.refreshToken(),
                        ClientRequestMetadata.from(httpRequest)
                )
        );
    }

    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> logout(
            @Valid @RequestBody LogoutRequest request,
            HttpServletRequest httpRequest
    ) {
        authService.logout(
                request.refreshToken(),
                ClientRequestMetadata.from(httpRequest)
        );
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/logout-all")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> logoutAll(
            Authentication authentication,
            HttpServletRequest httpRequest
    ) {
        authService.logoutAll(
                authentication.getName(),
                ClientRequestMetadata.from(httpRequest)
        );
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDto> currentUser(Authentication authentication) {
        return ResponseEntity.ok(authService.getCurrentUser(authentication.getName()));
    }
}
