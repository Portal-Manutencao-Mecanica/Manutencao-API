package com.weg.Maintenance_API.user.controller;

import com.weg.Maintenance_API.auth.dto.request.LoginRequestDto;
import com.weg.Maintenance_API.auth.dto.response.LoginResponseDto;
import com.weg.Maintenance_API.auth.service.AuthService;
import com.weg.Maintenance_API.user.dto.response.UserResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class UserController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto request){
        return new ResponseEntity<>(authService.login(request), HttpStatus.valueOf(200));
    }
    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> currentUser(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                authService.getCurrentUser(authentication.getName())
        );
    }
}