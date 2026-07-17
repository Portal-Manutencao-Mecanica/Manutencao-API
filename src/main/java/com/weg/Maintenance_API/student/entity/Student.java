package com.weg.Maintenance_API.student.entity;

import com.weg.Maintenance_API.enums.Role;
import com.weg.Maintenance_API.classgroup.entity.ClassGroup;
import com.weg.Maintenance_API.user.entity.User;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "student")
public class Student extends User {
    
    @ManyToMany(mappedBy = "students")
    private List<ClassGroup> classGroups = new ArrayList<>();

    public Student(String name, String email, Role role) {
        super(name, email, role);
    }
}


