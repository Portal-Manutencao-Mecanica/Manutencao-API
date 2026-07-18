package com.weg.Maintenance_API.classgroup.entity;

import com.weg.Maintenance_API.student.entity.Student;
import com.weg.Maintenance_API.teacher.entity.Teacher;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "class_group")
@Getter
@Setter
@NoArgsConstructor
public class ClassGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "class_group_id")
    private Long id;

    @Column(name = "acronym", nullable = false, unique = true, length = 30)
    private String acronym;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "class_group_teacher",
            joinColumns = @JoinColumn(name = "class_group_id"),
            inverseJoinColumns = @JoinColumn(name = "teacher_id"))
    private List<Teacher> teachers = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "class_group_student",
            joinColumns = @JoinColumn(name = "class_group_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id"))
    private List<Student> students = new ArrayList<>();
}
