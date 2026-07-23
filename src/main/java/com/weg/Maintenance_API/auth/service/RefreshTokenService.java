package com.weg.Maintenance_API.auth.service;

import com.weg.Maintenance_API.audit.service.AuditService;
import com.weg.Maintenance_API.auth.entity.RefreshToken;
import com.weg.Maintenance_API.auth.repository.RefreshTokenRepository;
import com.weg.Maintenance_API.exception.type.ExpiredTokenException;
import com.weg.Maintenance_API.exception.type.InvalidTokenException;
import com.weg.Maintenance_API.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final SecureTokenService secureTokenService;
    private final AuditService auditService;

    @Value("${app.jwt.refresh-expiration-seconds:2592000}")
    private long refreshExpirationSeconds;

    @Transactional
    public IssuedRefreshToken issue(User user, ClientRequestMetadata metadata) {
        return create(user, metadata);
    }

    @Transactional
    public RotatedRefreshToken rotate(String rawToken, ClientRequestMetadata metadata) {
        String tokenHash = secureTokenService.hash(rawToken);
        RefreshToken currentToken = refreshTokenRepository.findByTokenHashForUpdate(tokenHash)
                .orElseThrow(() -> new InvalidTokenException("Refresh token inválido."));

        LocalDateTime now = LocalDateTime.now();
        if (currentToken.isRevoked()) {
            throw new InvalidTokenException("Refresh token inválido.");
        }
        if (currentToken.isExpired(now)) {
            currentToken.revoke(now);
            throw new ExpiredTokenException("Refresh token expirado. Faça login novamente.");
        }

        User user = currentToken.getUser();
        validateUserCanRefresh(user);

        currentToken.revoke(now);
        IssuedRefreshToken nextToken = create(user, metadata);

        auditService.record(
                user,
                "TOKEN_REFRESHED",
                "USER",
                user.getId(),
                metadata.endpoint(),
                metadata.httpMethod(),
                metadata.ipAddress(),
                metadata.userAgent(),
                true,
                "Refresh token rotacionado."
        );
        return new RotatedRefreshToken(user, nextToken.rawToken(), nextToken.expiresAt());
    }

    @Transactional
    public void revoke(String rawToken, ClientRequestMetadata metadata) {
        String tokenHash = secureTokenService.hash(rawToken);
        refreshTokenRepository.findByTokenHashForUpdate(tokenHash).ifPresent(token -> {
            token.revoke(LocalDateTime.now());
            auditService.record(
                    token.getUser(),
                    "LOGOUT",
                    "USER",
                    token.getUser().getId(),
                    metadata.endpoint(),
                    metadata.httpMethod(),
                    metadata.ipAddress(),
                    metadata.userAgent(),
                    true,
                    "Sessão encerrada."
            );
        });
    }

    @Transactional
    public int revokeAll(UUID userId, ClientRequestMetadata metadata, User actor) {
        int revoked = refreshTokenRepository.revokeAllActiveByUserId(userId, LocalDateTime.now());
        auditService.record(
                actor,
                "LOGOUT_ALL",
                "USER",
                userId,
                metadata.endpoint(),
                metadata.httpMethod(),
                metadata.ipAddress(),
                metadata.userAgent(),
                true,
                "Sessões revogadas: " + revoked
        );
        return revoked;
    }

    @Transactional
    public int revokeAll(UUID userId) {
        return refreshTokenRepository.revokeAllActiveByUserId(userId, LocalDateTime.now());
    }

    private IssuedRefreshToken create(User user, ClientRequestMetadata metadata) {
        String rawToken = secureTokenService.generate();
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(refreshExpirationSeconds);
        refreshTokenRepository.save(new RefreshToken(
                user,
                secureTokenService.hash(rawToken),
                expiresAt,
                metadata.ipAddress(),
                metadata.userAgent()
        ));
        return new IssuedRefreshToken(rawToken, expiresAt);
    }

    private void validateUserCanRefresh(User user) {
        if (!user.isEnabled()
                || !user.isAccountNonLocked()
                || user.isTemporarilyLocked()
                || !user.getOrganization().isActive()) {
            throw new InvalidTokenException("A sessão não pode ser renovada. Faça login novamente.");
        }
    }

    public record IssuedRefreshToken(String rawToken, LocalDateTime expiresAt) {
    }

    public record RotatedRefreshToken(User user, String rawToken, LocalDateTime expiresAt) {
    }
}
