package com.weg.Maintenance_API.machinelog.entity;

import com.weg.Maintenance_API.classgroup.entity.ClassGroup;
import com.weg.Maintenance_API.enums.MaintenanceType;
import com.weg.Maintenance_API.enums.TaskCriticality;
import com.weg.Maintenance_API.enums.TaskSituation;
import com.weg.Maintenance_API.machine.entity.Machine;
import com.weg.Maintenance_API.media.entity.Media;
import com.weg.Maintenance_API.place.entity.Place;
import com.weg.Maintenance_API.student.entity.Student;
import com.weg.Maintenance_API.teacher.dto.response.TeacherResponseDto;
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
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "machine_log")
@Getter
@Setter
@NoArgsConstructor
public class MachineLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "machine_log_id")
    private Long id;

    @Column(name = "number_card", nullable = false, unique = true, length = 255)
    private String numberCard = java.util.UUID.randomUUID().toString();

    @Column(name = "title", length = 150)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "execution_report", columnDefinition = "TEXT")
    private String executionReport;

    @Enumerated(EnumType.STRING)
    @Column(name = "task_situation", nullable = false, length = 30)
    private TaskSituation taskSituation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "machine_id", nullable = false)
    private Machine machine;

    @Column(name = "service_performed", columnDefinition = "TEXT")
    private String servicePerformed;

    @Column(name = "teacher_concluded_at")
    private LocalDateTime teacherConcludedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsible_teacher_id")
    private Teacher responsibleTeacher;

    @Column(name = "registered_at", nullable = false, updatable = false)
    private LocalDateTime registeredAt;

    @Column(name = "execution_started_at")
    private LocalDateTime executionStartedAt;

    @Column(name = "execution_ended_at")
    private LocalDateTime executionEndedAt;

    @Column(name = "planned_action", columnDefinition = "TEXT")
    private String plannedAction;

    @Enumerated(EnumType.STRING)
    @Column(name = "task_criticality", nullable = false, length = 30)
    private TaskCriticality taskCriticality;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    private Place place;

    @Enumerated(EnumType.STRING)
    @Column(name = "maintenance_type", length = 30)
    private MaintenanceType maintenanceType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_group_id")
    private ClassGroup classGroup;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "machine_log_student",
            joinColumns = @JoinColumn(name = "machine_log_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    private List<Student> assignedStudents = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(
            name = "machine_log_media",
            joinColumns = @JoinColumn(name = "machine_log_id"),
            inverseJoinColumns = @JoinColumn(name = "media_id")
    )
    private List<Media> media = new ArrayList<>();

    @Column(name = "report_link", length = 2048)
    private String reportLink;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdBy;

    @PrePersist
    protected void onCreate() {
        if (registeredAt == null) {
            registeredAt = LocalDateTime.now();
        }
    }
}
