package com.weg.Manutencao_API.aluno.entity;

import com.weg.Manutencao_API.enums.Role;
import com.weg.Manutencao_API.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "student")
public class Student extends User {
    public Student() {
        super();
    }

    public Student(String name, String email) {
        super(name, email, Role.ALUNO);
    }
}