package com.weg.Maintenance_API.auth.password.repository;

import com.weg.Maintenance_API.auth.password.entity.PasswordResetToken;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface PasswordResetTokenRepository
        extends JpaRepository<PasswordResetToken, UUID> {

    @Query("""
            select token
              from PasswordResetToken token
              join fetch token.user user
              join fetch user.organization
             where token.tokenHash = :tokenHash
            """)
    Optional<PasswordResetToken> findByTokenHash(
            @Param("tokenHash") String tokenHash
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select token
              from PasswordResetToken token
              join fetch token.user user
              join fetch user.organization
             where token.tokenHash = :tokenHash
            """)
    Optional<PasswordResetToken> findByTokenHashForUpdate(
            @Param("tokenHash") String tokenHash
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update PasswordResetToken token
               set token.usedAt = :invalidatedAt
             where token.user.id = :userId
               and token.usedAt is null
            """)
    int invalidateAllActiveByUserId(
            @Param("userId") UUID userId,
            @Param("invalidatedAt") LocalDateTime invalidatedAt
    );
}
