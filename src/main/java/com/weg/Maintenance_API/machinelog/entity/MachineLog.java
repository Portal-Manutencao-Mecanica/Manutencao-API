package com.weg.Maintenance_API.machinelog.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.weg.Maintenance_API.student.entity.Student;
import com.weg.Maintenance_API.enums.MaintenanceType;
import com.weg.Maintenance_API.enums.TaskCriticality;
import com.weg.Maintenance_API.enums.TaskSituation;
import com.weg.Maintenance_API.place.entity.Place;
import com.weg.Maintenance_API.machine.entity.Machine;
import com.weg.Maintenance_API.teacher.entity.Teacher;
import com.weg.Maintenance_API.classgroup.entity.ClassGroup;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "machine_log")
public class MachineLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "machine_log_id")
    private Long id;

    @Column(name = "machine_log_title")
    private String title;

    @Column(name = "machine_log_description")
    private String description;

    @Column(name = "machine_log_content", columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "task_situation", nullable = false)
    private TaskSituation taskSituation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "machine_id", nullable = false)
    private Machine machine;

    @Column(name = "machine_log_service_execute")
    private String serviceExecute;

    @Column(name = "machine_log_conclusion")
    private LocalDateTime conclusion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "scheduled_for", nullable = false)
    private LocalDateTime scheduledFor;

    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;

    @Column(name = "action_to_execute", columnDefinition = "TEXT")
    private String actionToExecute;

    @Enumerated(EnumType.STRING)
    @Column(name = "task_criticality", nullable = false)
    private TaskCriticality taskCriticality;

    @Column(name = "image", columnDefinition = "bytea")
    private byte[] image;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    private Place place;

    @Enumerated(EnumType.STRING)
    @Column(name = "maintenance_type")
    private MaintenanceType maintenanceType;

    @Column(name = "registration_period")
    private String registrationPeriod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id")
    private ClassGroup classGroup;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "machine_log_student",
            joinColumns = @JoinColumn(name = "machine_log_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id"))
    private List<Student> assignedStudents = new ArrayList<>();

    @Column(name = "report_link", length = 2048)
    private String reportLink;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

