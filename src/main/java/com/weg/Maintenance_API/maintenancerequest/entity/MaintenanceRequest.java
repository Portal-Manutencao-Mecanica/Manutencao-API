package com.weg.Maintenance_API.maintenancerequest.entity;

import com.weg.Maintenance_API.student.entity.Student;
import com.weg.Maintenance_API.designation.entity.Designation;
import com.weg.Maintenance_API.enums.MaintenanceRequestStatus;
import com.weg.Maintenance_API.enums.Priority;
import com.weg.Maintenance_API.place.entity.Place;
import com.weg.Maintenance_API.machine.entity.Machine;
import com.weg.Maintenance_API.teacher.entity.Teacher;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "maintenance_request")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "maintenance_request_id")
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MaintenanceRequestStatus status;

    @ManyToOne(optional = false)
    @JoinColumn(name = "designation_id", nullable = false)
    private Designation designation;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    private Priority priority;

    @ManyToMany
    @JoinTable(name = "maintenance_request_student",
            joinColumns = @JoinColumn(name = "maintenance_request_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id"))
    private List<Student> students = new ArrayList<>();

    @ManyToOne(optional = false)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @ElementCollection
    @CollectionTable(name = "maintenance_request_image",
            joinColumns = @JoinColumn(name = "maintenance_request_id"))
    @Column(name = "image_data", nullable = false, columnDefinition = "bytea")
    private List<byte[]> anomalyImages = new ArrayList<>();

    @Column(name = "patrimony")
    private String patrimony;

    @Column(name = "tag")
    private String tag;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime dateTime;

    @ManyToOne(optional = false)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    @ManyToOne(optional = false)
    @JoinColumn(name = "machine_id", nullable = false)
    private Machine equipment;
}


