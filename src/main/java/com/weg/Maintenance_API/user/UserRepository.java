package com.weg.Maintenance_API.user;


import java.util.UUID;

import com.weg.Maintenance_API.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    @EntityGraph(attributePaths = "organization")
    Optional<User> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
    Optional<User> findByUsernameIgnoreCase(String username);
    boolean existsByUsernameIgnoreCase(String username);
    long countByRoleAndEnabledTrueAndAccountNonLockedTrue(
            com.weg.Maintenance_API.enums.Role role
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select user
              from User user
             where lower(user.email) = lower(:email)
            """)
    Optional<User> findByEmailForUpdate(@Param("email") String email);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select user
              from User user
             where user.id = :id
            """)
    Optional<User> findByIdForUpdate(@Param("id") UUID id);
}
