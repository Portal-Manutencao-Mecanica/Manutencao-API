package com.weg.Maintenance_API.auth.firstaccess.repository;

import com.weg.Maintenance_API.auth.firstaccess.entity.FirstAccessCode;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface FirstAccessCodeRepository extends JpaRepository<FirstAccessCode, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<FirstAccessCode> findFirstByUserIdAndUsedAtIsNullOrderByCreatedAtDesc(UUID userId);

    Optional<FirstAccessCode> findFirstByUserIdOrderByCreatedAtDesc(UUID userId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update FirstAccessCode code
               set code.usedAt = :invalidatedAt
             where code.user.id = :userId
               and code.usedAt is null
            """)
    int invalidateAllActiveByUserId(
            @Param("userId") UUID userId,
            @Param("invalidatedAt") LocalDateTime invalidatedAt
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from FirstAccessCode item where item.user.id = :userId")
    void deleteByUserId(@Param("userId") UUID userId);
}
