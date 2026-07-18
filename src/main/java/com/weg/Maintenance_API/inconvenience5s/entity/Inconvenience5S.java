package com.weg.Maintenance_API.inconvenience5s.entity;

import com.weg.Maintenance_API.classgroup.entity.ClassGroup;
import com.weg.Maintenance_API.enums.Inconvenience5SStatus;
import com.weg.Maintenance_API.enums.RegistrationPeriod;
import com.weg.Maintenance_API.media.entity.Media;
import com.weg.Maintenance_API.place.entity.Place;
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
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "inconvenience_5s")
@Getter
@Setter
@NoArgsConstructor
public class Inconvenience5S {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inconvenience_5s_id")
    private Long id;

    @Column(name = "inconvenience", nullable = false, length = 255)
    private String inconvenience;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private Inconvenience5SStatus status = Inconvenience5SStatus.NAO_VISUALIZADA;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "notified_teacher_id", nullable = false)
    private Teacher notifiedTeacher;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "class_group_id", nullable = false)
    private ClassGroup classGroup;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdBy;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "inconvenience_5s_student",
            joinColumns = @JoinColumn(name = "inconvenience_5s_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    private List<Student> involvedStudents = new ArrayList<>();

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(
            name = "inconvenience_5s_media",
            joinColumns = @JoinColumn(name = "inconvenience_5s_id"),
            inverseJoinColumns = @JoinColumn(name = "media_id")
    )
    private List<Media> media = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "registration_period", nullable = false, length = 30)
    private RegistrationPeriod registrationPeriod;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = Inconvenience5SStatus.NAO_VISUALIZADA;
        }
    }
}
