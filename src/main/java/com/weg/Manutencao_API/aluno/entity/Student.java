package com.weg.Manutencao_API.aluno.entity;

import com.weg.Manutencao_API.enums.Role;
import com.weg.Manutencao_API.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "student")
public class Student extends User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_id")
    private Long id;

    public Student(String name, String email, Role role) {
        super(name, email, role);
    }
}