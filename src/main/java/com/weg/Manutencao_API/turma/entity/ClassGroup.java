package com.weg.Manutencao_API.turma.entity;

import java.util.ArrayList;
import java.util.List;

import com.weg.Manutencao_API.aluno.entity.Student;
import com.weg.Manutencao_API.professor.entity.Teacher;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "class_group")
public class ClassGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "class_group_id")
    private Long id;

    @ManyToMany
    @JoinTable(name = "class_group_teacher",
            joinColumns = @JoinColumn(name = "class_group_id"),
            inverseJoinColumns = @JoinColumn(name = "teacher_id"))
    private List<Teacher> teachers = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "class_group_student",
            joinColumns = @JoinColumn(name = "class_group_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id"))
    private List<Student> students = new ArrayList<>();

    @Column(name = "acronym_class")
    private String acronym;

}
