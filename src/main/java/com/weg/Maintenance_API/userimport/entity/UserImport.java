package com.weg.Maintenance_API.userimport.entity;

import com.weg.Maintenance_API.organization.entity.Organization;
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
@Table(name = "user_import")
@Getter
@Setter
@NoArgsConstructor
public class UserImport {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_import_id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "filename", nullable = false, length = 255, updatable = false)
    private String filename;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "imported_by", nullable = false, updatable = false)
    private User importedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", updatable = false)
    private Organization organization;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 40)
    private UserImportStatus status;

    @Column(name = "total_rows", nullable = false)
    private int totalRows;

    @Column(name = "created_count", nullable = false)
    private int createdCount;

    @Column(name = "failed_count", nullable = false)
    private int failedCount;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    public UserImport(
            String filename,
            User importedBy,
            Organization organization,
            int totalRows
    ) {
        this.filename = filename;
        this.importedBy = importedBy;
        this.organization = organization;
        this.totalRows = totalRows;
        this.status = UserImportStatus.PROCESSING;
    }

    public void complete(int created, int failed) {
        this.createdCount = created;
        this.failedCount = failed;
        this.completedAt = LocalDateTime.now();
        this.status = failed == 0
                ? UserImportStatus.COMPLETED
                : UserImportStatus.COMPLETED_WITH_ERRORS;
    }

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = UserImportStatus.PROCESSING;
        }
    }
}
