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

    // Valida a regra aplicada por este metodo.
    @Transactional
    public IssuedRefreshToken issue(User user, ClientRequestMetadata metadata) {
        return create(user, metadata);
    }

    // Executa a operacao deste metodo.
    @Transactional
    public RotatedRefreshToken rotate(String rawToken, ClientRequestMetadata metadata) {
        String tokenHash = secureTokenService.hash(rawToken);
        RefreshToken currentToken = refreshTokenRepository.findByTokenHashForUpdate(tokenHash)
                .orElseThrow(() -> new InvalidTokenException("Refresh token invÃ¡lido."));

        LocalDateTime now = LocalDateTime.now();
        if (currentToken.isRevoked()) {
            throw new InvalidTokenException("Refresh token invÃ¡lido.");
        }
        if (currentToken.isExpired(now)) {
            currentToken.revoke(now);
            throw new ExpiredTokenException("Refresh token expirado. FaÃ§a login novamente.");
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

    // Remove ou invalida os dados solicitados.
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
                    "SessÃ£o encerrada."
            );
        });
    }

    // Remove ou invalida os dados solicitados.
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
                "SessÃµes revogadas: " + revoked
        );
        return revoked;
    }

    // Remove ou invalida os dados solicitados.
    @Transactional
    public int revokeAll(UUID userId) {
        return refreshTokenRepository.revokeAllActiveByUserId(userId, LocalDateTime.now());
    }

    // Cria e persiste os dados da operacao.
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

    // Valida a regra aplicada por este metodo.
    private void validateUserCanRefresh(User user) {
        if (!user.isEnabled()
                || !user.isAccountNonLocked()
                || user.isTemporarilyLocked()
                || user.isPasswordChangeRequired()
                || !user.getOrganization().isActive()) {
            throw new InvalidTokenException("A sessÃ£o nÃ£o pode ser renovada. FaÃ§a login novamente.");
        }
    }

    // Valida a regra aplicada por este metodo.
    public record IssuedRefreshToken(String rawToken, LocalDateTime expiresAt) {
    }

    public record RotatedRefreshToken(User user, String rawToken, LocalDateTime expiresAt) {
    }
}
