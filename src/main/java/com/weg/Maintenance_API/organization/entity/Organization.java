package com.weg.Maintenance_API.organization.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;

@Entity
@Table(
        name = "organization",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_organization_name", columnNames = "name"),
                @UniqueConstraint(name = "uk_organization_email_domain", columnNames = "email_domain")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "organization_id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 30)
    private OrganizationType type;

    @Column(name = "email_domain", nullable = false, length = 150)
    private String emailDomain;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    public Organization(String name, OrganizationType type, String emailDomain) {
        this.name = name;
        this.type = type;
        this.emailDomain = normalizeDomain(emailDomain);
    }

    public boolean acceptsEmail(String email) {
        if (email == null || !email.contains("@")) {
            return false;
        }
        String domain = email.substring(email.lastIndexOf('@') + 1).toLowerCase(Locale.ROOT);
        String normalizedDomain = normalizeDomain(emailDomain);
        return domain.equals(normalizedDomain) || domain.endsWith("." + normalizedDomain);
    }

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
        emailDomain = normalizeDomain(emailDomain);
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
        emailDomain = normalizeDomain(emailDomain);
    }

    private static String normalizeDomain(String domain) {
        return domain == null
                ? null
                : domain.trim().toLowerCase(Locale.ROOT).replaceFirst("^@", "");
    }
}
