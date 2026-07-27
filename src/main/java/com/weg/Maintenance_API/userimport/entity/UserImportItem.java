package com.weg.Maintenance_API.userimport.entity;

import com.weg.Maintenance_API.enums.Role;
import com.weg.Maintenance_API.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_import_item")
@Getter
@Setter
@NoArgsConstructor
public class UserImportItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_import_item_id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_import_id", nullable = false, updatable = false)
    private UserImport userImport;

    @Column(name = "row_number", nullable = false, updatable = false)
    private int rowNumber;

    @Column(name = "name", length = 150, updatable = false)
    private String name;

    @Column(name = "username", length = 50, updatable = false)
    private String username;

    @Column(name = "email", length = 150, updatable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 30, updatable = false)
    private Role role;

    @Column(name = "organization_value", length = 150, updatable = false)
    private String organizationValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private UserImportItemStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_user_id", updatable = false)
    private User createdUser;

    @Column(name = "error_code", length = 80, updatable = false)
    private String errorCode;

    @Column(name = "error_field", length = 80, updatable = false)
    private String errorField;

    @Column(name = "error_message", length = 500, updatable = false)
    private String errorMessage;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public static UserImportItem success(
            UserImport userImport,
            int rowNumber,
            String name,
            String username,
            String email,
            Role role,
            String organizationValue,
            User createdUser
    ) {
        UserImportItem item = base(
                userImport, rowNumber, name, username, email, role, organizationValue
        );
        item.status = UserImportItemStatus.CREATED;
        item.createdUser = createdUser;
        return item;
    }

    public static UserImportItem failure(
            UserImport userImport,
            int rowNumber,
            String name,
            String username,
            String email,
            Role role,
            String organizationValue,
            String errorCode,
            String errorField,
            String errorMessage
    ) {
        UserImportItem item = base(
                userImport, rowNumber, name, username, email, role, organizationValue
        );
        item.status = UserImportItemStatus.FAILED;
        item.errorCode = errorCode;
        item.errorField = errorField;
        item.errorMessage = errorMessage;
        return item;
    }

    private static UserImportItem base(
            UserImport userImport,
            int rowNumber,
            String name,
            String username,
            String email,
            Role role,
            String organizationValue
    ) {
        UserImportItem item = new UserImportItem();
        item.userImport = userImport;
        item.rowNumber = rowNumber;
        item.name = name;
        item.username = username;
        item.email = email;
        item.role = role;
        item.organizationValue = organizationValue;
        return item;
    }

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
