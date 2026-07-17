package com.weg.Manutencao_API.professor.entity;

import java.util.ArrayList;
import java.util.List;

import com.weg.Manutencao_API.enums.Role;
import com.weg.Manutencao_API.turma.entity.ClassGroup;
import com.weg.Manutencao_API.user.entity.User;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "teacher")
public class Teacher extends User {
    
    @ManyToMany(mappedBy = "teachers")
    private List<ClassGroup> classGroups = new ArrayList<>();

    public Teacher(String name, String email, Role role, List<ClassGroup> classGroups) {
        super(name, email, role);
        this.classGroups = classGroups;
    }
}
