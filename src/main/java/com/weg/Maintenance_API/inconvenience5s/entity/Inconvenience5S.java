package com.weg.Maintenance_API.inconvenience5s.entity;

import com.weg.Maintenance_API.student.entity.Student;
import com.weg.Maintenance_API.place.entity.Place;
import com.weg.Maintenance_API.teacher.entity.Teacher;
import com.weg.Maintenance_API.classgroup.entity.ClassGroup;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "inconvenience_5s")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Inconvenience5S {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inconvenience_5s_id")
    private Integer id;

    @Column(name = "inconvenience", nullable = false)
    private String inconvenience;

    @Column(name = "status", nullable = false)
    private Boolean status;

    @ManyToOne(optional = false)
    @JoinColumn(name = "place_id", nullable = false)
    private Place room;

    @ManyToOne(optional = false)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    @Column(name = "event_date", nullable = false)
    private LocalDate dateTime;

    @ManyToOne(optional = false)
    @JoinColumn(name = "class_id", nullable = false)
    private ClassGroup classGroup;

    @ManyToMany
    @JoinTable(name = "inconvenience_5s_student",
            joinColumns = @JoinColumn(name = "inconvenience_5s_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id"))
    private List<Student> students = new ArrayList<>();

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ElementCollection
    @CollectionTable(name = "inconvenience_5s_image",
            joinColumns = @JoinColumn(name = "inconvenience_5s_id"))
    @Column(name = "image_url", nullable = false, length = 2048)
    private List<String> images = new ArrayList<>();

    @Column(name = "registered_occasion")
    private String registeredOccasion;
}


