package com.weg.Maintenance_API.auth.service;

import com.weg.Maintenance_API.audit.service.AuditService;
import com.weg.Maintenance_API.auth.dto.request.LoginRequestDto;
import com.weg.Maintenance_API.auth.dto.response.LoginResponseDto;
import com.weg.Maintenance_API.exception.type.ResourceNotFoundException;
import com.weg.Maintenance_API.user.UserRepository;
import com.weg.Maintenance_API.user.dto.response.UserResponseDto;
import com.weg.Maintenance_API.user.entity.User;
import com.weg.Maintenance_API.user.mapper.UserResponseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtTokenService jwtTokenService;
    private final RefreshTokenService refreshTokenService;
    private final LoginAttemptService loginAttemptService;
    private final UserResponseMapper userResponseMapper;
    private final AuditService auditService;

    @Transactional
    public LoginResponseDto login(
            LoginRequestDto request,
            ClientRequestMetadata metadata
    ) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, request.password())
            );
        } catch (AuthenticationException exception) {
            if (exception instanceof BadCredentialsException) {
                loginAttemptService.recordFailure(email, metadata);
            } else {
                recordDeniedLogin(email, metadata, exception);
            }
            throw new BadCredentialsException("Credenciais inválidas.");
        }

        User user = userRepository.findByEmailIgnoreCase(authentication.getName())
                .orElseThrow(() -> new IllegalStateException(
                        "O usuário autenticado não foi localizado."
                ));

        validateLoginState(user);
        loginAttemptService.recordSuccess(user.getId());

        JwtTokenService.TokenData accessToken = jwtTokenService.generateToken(user);
        RefreshTokenService.IssuedRefreshToken refreshToken =
                refreshTokenService.issue(user, metadata);

        auditService.record(
                user,
                "LOGIN_SUCCESS",
                "USER",
                user.getId(),
                metadata.endpoint(),
                metadata.httpMethod(),
                metadata.ipAddress(),
                metadata.userAgent(),
                true,
                "Autenticação concluída."
        );
        return response(user, accessToken, refreshToken.rawToken());
    }

    public LoginResponseDto refresh(
            String rawRefreshToken,
            ClientRequestMetadata metadata
    ) {
        RefreshTokenService.RotatedRefreshToken rotatedToken =
                refreshTokenService.rotate(rawRefreshToken, metadata);
        JwtTokenService.TokenData accessToken =
                jwtTokenService.generateToken(rotatedToken.user());
        return response(rotatedToken.user(), accessToken, rotatedToken.rawToken());
    }

    public void logout(String rawRefreshToken, ClientRequestMetadata metadata) {
        refreshTokenService.revoke(rawRefreshToken, metadata);
    }

    @Transactional
    public void logoutAll(String email, ClientRequestMetadata metadata) {
        User user = getUserByEmail(email);
        refreshTokenService.revokeAll(user.getId(), metadata, user);
    }

    @Transactional(readOnly = true)
    public UserResponseDto getCurrentUser(String email) {
        return userResponseMapper.toResponse(getUserByEmail(email));
    }

    private LoginResponseDto response(
            User user,
            JwtTokenService.TokenData accessToken,
            String rawRefreshToken
    ) {
        return new LoginResponseDto(
                accessToken.accessToken(),
                rawRefreshToken,
                "Bearer",
                accessToken.expiresIn(),
                user.isPasswordChangeRequired(),
                userResponseMapper.toResponse(user)
        );
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário autenticado"));
    }

    private void validateLoginState(User user) {
        if (!user.getOrganization().isActive()) {
            throw new BadCredentialsException("Credenciais inválidas.");
        }
        if (user.isPasswordChangeRequired()
                && user.getTemporaryPasswordExpiresAt() != null
                && !user.getTemporaryPasswordExpiresAt().isAfter(LocalDateTime.now())) {
            throw new CredentialsExpiredException(
                    "A senha temporária expirou. Solicite novas credenciais."
            );
        }
    }

    private void recordDeniedLogin(
            String email,
            ClientRequestMetadata metadata,
            AuthenticationException exception
    ) {
        User user = userRepository.findByEmailIgnoreCase(email).orElse(null);
        if (user == null) {
            auditService.recordAnonymous(
                    email,
                    "LOGIN_DENIED",
                    metadata.endpoint(),
                    metadata.httpMethod(),
                    metadata.ipAddress(),
                    metadata.userAgent(),
                    false,
                    exception.getClass().getSimpleName()
            );
            return;
        }
        auditService.record(
                user,
                "LOGIN_DENIED",
                "USER",
                user.getId(),
                metadata.endpoint(),
                metadata.httpMethod(),
                metadata.ipAddress(),
                metadata.userAgent(),
                false,
                exception.getClass().getSimpleName()
        );
    }
}
