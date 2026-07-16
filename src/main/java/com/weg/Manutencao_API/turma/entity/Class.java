package com.weg.Manutencao_API.turma.entity;

import java.util.List;

import com.weg.Manutencao_API.aluno.entity.Student;
import com.weg.Manutencao_API.enums.Role;
import com.weg.Manutencao_API.professor.entity.Teacher;
import com.weg.Manutencao_API.usuario.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "class")
public class Class extends User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "class_id")
    private Long id;

    @ManyToMany
    private List<Teacher> teachers;

    @ManyToMany
    private List<Student> students;

    @Column(name = "acronym_class")
    private String acronym;

    public Class(String name, String email, Role role, List<Teacher> teachers, List<Student> students, String acronym) {
        super(name, email, role);
        this.teachers = teachers;
        this.students = students;
        this.acronym = acronym;
    }

}
