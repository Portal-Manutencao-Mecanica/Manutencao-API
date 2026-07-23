package com.weg.Maintenance_API.auth.service;

import com.weg.Maintenance_API.exception.type.ResourceNotFoundException;

import com.weg.Maintenance_API.auth.dto.request.LoginRequestDto;
import com.weg.Maintenance_API.auth.dto.response.LoginResponseDto;
import com.weg.Maintenance_API.user.UserRepository;
import com.weg.Maintenance_API.user.dto.response.UserResponseDto;
import com.weg.Maintenance_API.user.entity.User;
import com.weg.Maintenance_API.user.mapper.UserResponseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtTokenService jwtTokenService;
    private final UserResponseMapper userResponseMapper;

    public LoginResponseDto login(LoginRequestDto request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        User user = userRepository.findByEmailIgnoreCase(authentication.getName())
                .orElseThrow(() -> new IllegalStateException(
                        "Authenticated user was not found"
                ));

        JwtTokenService.TokenData tokenData =
                jwtTokenService.generateToken(user);

        UserResponseDto userResponse = userResponseMapper.toResponse(user);

        return new LoginResponseDto(
                tokenData.accessToken(),
                "Bearer",
                tokenData.expiresIn(),
                userResponse
        );
    }

    public UserResponseDto getCurrentUser(String email) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Usuário autenticado"));

        return userResponseMapper.toResponse(user);
    }
}
