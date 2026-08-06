package com.weg.Maintenance_API.user;


import java.util.UUID;

import com.weg.Maintenance_API.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.List;
import com.weg.Maintenance_API.enums.Role;

@Repository
public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {
    @EntityGraph(attributePaths = "organization")
    Optional<User> findByEmailIgnoreCase(String email);
    Optional<User> findByEmailIgnoreCaseOrUsernameIgnoreCase(String email, String username);
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCaseAndIdNot(String email, UUID id);
    boolean existsByNumberCardIgnoreCaseAndIdNot(String numberCard, UUID id);
    boolean existsByNumberCardIgnoreCase(String numberCard);
    Optional<User> findByUsernameIgnoreCase(String username);
    boolean existsByUsernameIgnoreCase(String username);
    List<User> findAllByRoleAndEnabledTrueAndAccountNonLockedTrue(Role role);
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
    @EntityGraph(attributePaths = "organization")
    @Query("""
            select user
              from User user
             where lower(user.email) = lower(:identifier)
                or lower(user.username) = lower(:identifier)
            """)
    Optional<User> findByLoginIdentifierForUpdate(@Param("identifier") String identifier);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = "organization")
    @Query("""
            select user
              from User user
             where user.id = :id
            """)
    Optional<User> findByIdForUpdate(@Param("id") UUID id);
}
