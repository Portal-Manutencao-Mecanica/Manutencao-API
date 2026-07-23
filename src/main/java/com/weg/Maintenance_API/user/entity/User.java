package com.weg.Maintenance_API.user.entity;

import com.weg.Maintenance_API.enums.Role;
import com.weg.Maintenance_API.organization.entity.Organization;
import com.weg.Maintenance_API.media.entity.Media;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "user_name", nullable = false, length = 150)
    private String name;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "user_email", nullable = false, unique = true, length = 150)
    private String email;

    @JsonIgnore
    @Column(name = "user_password", nullable = false, length = 100)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", nullable = false, length = 30)
    private Role role;

    @Column(name = "user_enabled", nullable = false)
    private boolean enabled = true;

    @Column(name = "account_non_locked", nullable = false)
    private boolean accountNonLocked = true;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @Column(name = "password_change_required", nullable = false)
    private boolean passwordChangeRequired;

    @Column(name = "temporary_password_expires_at")
    private LocalDateTime temporaryPasswordExpiresAt;

    @Column(name = "password_changed_at")
    private LocalDateTime passwordChangedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_photo_media_id", unique = true)
    private Media profilePhoto;

    @Column(name = "failed_login_attempts", nullable = false)
    private int failedLoginAttempts;

    @Column(name = "locked_until")
    private LocalDateTime lockedUntil;

    @Column(name = "last_failed_login_at")
    private LocalDateTime lastFailedLoginAt;

    @Column(name = "lockout_count", nullable = false)
    private int lockoutCount;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "number_card",nullable = false,unique = true)
    private String numberCard = UUID.randomUUID().toString();

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    protected User(String name, String email, String password, Role role) {
        this(name, email, password, role, UUID.randomUUID().toString());
    }

    protected User(String name, String email, String password, Role role,String numberCard) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.numberCard = numberCard;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
        if (username == null && email != null) {
            username = email.substring(0, email.indexOf('@')).toLowerCase();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Transient
    public boolean isTemporarilyLocked() {
        return lockedUntil != null && lockedUntil.isAfter(LocalDateTime.now());
    }

    @Transient
    public UserAccountStatus getStatus() {
        if (!enabled) {
            return UserAccountStatus.DISABLED;
        }
        if (!accountNonLocked) {
            return UserAccountStatus.BLOCKED;
        }
        if (isTemporarilyLocked()) {
            return UserAccountStatus.TEMPORARILY_LOCKED;
        }
        if (passwordChangeRequired
                && temporaryPasswordExpiresAt != null
                && temporaryPasswordExpiresAt.isBefore(LocalDateTime.now())) {
            return UserAccountStatus.PASSWORD_EXPIRED;
        }
        if (passwordChangeRequired) {
            return UserAccountStatus.PENDING_FIRST_ACCESS;
        }
        return UserAccountStatus.ACTIVE;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", enabled=" + enabled +
                ", accountNonLocked=" + accountNonLocked +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", numberCard=" + numberCard +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof User other)) {
            return false;
        }
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
