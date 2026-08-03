package com.weg.Maintenance_API.autonomousmaintenance.entity;

import com.weg.Maintenance_API.enums.AutonomousMaintenanceStatus;
import com.weg.Maintenance_API.enums.EquipmentCondition;
import com.weg.Maintenance_API.enums.EquipmentSituation;
import com.weg.Maintenance_API.event.entity.Event;
import com.weg.Maintenance_API.machine.entity.Machine;
import com.weg.Maintenance_API.media.entity.Media;
import com.weg.Maintenance_API.student.entity.Student;
import com.weg.Maintenance_API.teacher.entity.Teacher;
import com.weg.Maintenance_API.user.entity.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "autonomous_maintenance")
@Getter
@Setter
@NoArgsConstructor
public class AutonomousMaintenance {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "autonomous_maintenance_id", nullable = false, updatable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "equipment_situation", nullable = false, length = 30)
    private EquipmentSituation equipmentSituation;

    @Column(name = "scheduled_for", nullable = false)
    private LocalDateTime scheduledFor;

    @Column(name = "inspected_at")
    private LocalDateTime inspectedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "inspected_machine_id", nullable = false)
    private Machine inspectedMachine;

    @Enumerated(EnumType.STRING)
    @Column(name = "equipment_condition", nullable = false, length = 30)
    private EquipmentCondition equipmentCondition;

    @Column(name = "identified_nonconformities", columnDefinition = "TEXT")
    private String identifiedNonconformities;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "responsible_teacher_id", nullable = false, updatable = false)
    private Teacher responsibleTeacher;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "autonomous_maintenance_students",
            joinColumns = @JoinColumn(name = "autonomous_maintenance_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    private List<Student> assignedStudents = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by_user_id", nullable = false, updatable = false)
    private User createdBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private AutonomousMaintenanceStatus status =
            AutonomousMaintenanceStatus.PENDENTE_APROVACAO_COORDENADOR;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coordinator_approver_user_id")
    private User coordinatorApprover;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_event_id", unique = true)
    private Event calendarEvent;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(
            name = "autonomous_maintenance_media",
            joinColumns = @JoinColumn(name = "autonomous_maintenance_id"),
            inverseJoinColumns = @JoinColumn(name = "media_id")
    )
    private List<Media> media = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public void setAssignedStudents(List<Student> assignedStudents) {
        this.assignedStudents = assignedStudents == null
                ? new ArrayList<>()
                : new ArrayList<>(assignedStudents);
    }

    public void setMedia(List<Media> media) {
        this.media = media == null ? new ArrayList<>() : media;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        updatedAt = now;
        if (status == null) {
            status = AutonomousMaintenanceStatus.PENDENTE_APROVACAO_COORDENADOR;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
