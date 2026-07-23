package com.weg.Maintenance_API.autonomousmaintenance.entity;

import com.weg.Maintenance_API.enums.EquipmentCondition;
import com.weg.Maintenance_API.enums.EquipmentSituation;
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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "autonomous_maintenance")
@Getter
@Setter
@NoArgsConstructor
public class AutonomousMaintenance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "autonomous_maintenance_id")
    private Long id;

    @Column(name = "number_card", nullable = false, unique = true, length = 255)
    private String numberCard = java.util.UUID.randomUUID().toString();

    @Enumerated(EnumType.STRING)
    @Column(name = "equipment_situation", nullable = false, length = 30)
    private EquipmentSituation equipmentSituation;

    @Column(name = "inspected_at", nullable = false)
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
    @JoinColumn(name = "responsible_teacher_id", nullable = false)
    private Teacher responsibleTeacher;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "responsible_student_id", nullable = false)
    private Student responsibleStudent;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdBy;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(
            name = "autonomous_maintenance_media",
            joinColumns = @JoinColumn(name = "autonomous_maintenance_id"),
            inverseJoinColumns = @JoinColumn(name = "media_id")
    )
    private List<Media> media = new ArrayList<>();

    public void setMedia(List<Media> media) {
        this.media = media != null ? media : new ArrayList<>();
    }

    @PrePersist
    protected void onCreate() {
        if (inspectedAt == null) {
            inspectedAt = LocalDateTime.now();
        }
    }
}
