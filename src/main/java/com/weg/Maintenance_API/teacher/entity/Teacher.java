package com.weg.Maintenance_API.teacher.entity;

import java.util.ArrayList;
import java.util.List;

import com.weg.Maintenance_API.enums.Role;
import com.weg.Maintenance_API.classgroup.entity.ClassGroup;
import com.weg.Maintenance_API.user.entity.User;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "teacher")
public class Teacher extends User {
    
    @ManyToMany(mappedBy = "teachers", fetch = FetchType.LAZY)
    private List<ClassGroup> classGroups = new ArrayList<>();

    public Teacher() {
        setRole(Role.PROFESSOR);
    }

    public Teacher(String name, String email, String password) {
        super(name, email, password, Role.PROFESSOR);
    }
}
