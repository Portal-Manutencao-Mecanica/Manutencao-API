package com.weg.Manutencao_API.professor.entity;

import java.util.List;

import com.weg.Manutencao_API.enums.Role;
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
@Table(name = "teacher")
public class Teacher extends User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "teacher_id")
    private Long id;

    @ManyToMany
    private List<Turma> turmas;

    public Teacher(String name, String email, Role role, List<Turma> turmas) {
        super(name, email, role);
        this.turmas = turmas;
    }
}
