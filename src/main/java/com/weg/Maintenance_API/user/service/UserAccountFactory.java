package com.weg.Maintenance_API.user.service;

import com.weg.Maintenance_API.admin.entity.Admin;
import com.weg.Maintenance_API.coordinator.entity.Coordinator;
import com.weg.Maintenance_API.enums.Role;
import com.weg.Maintenance_API.organization.entity.Organization;
import com.weg.Maintenance_API.student.entity.Student;
import com.weg.Maintenance_API.teacher.entity.Teacher;
import com.weg.Maintenance_API.user.entity.User;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserAccountFactory {

    public User create(
            String name,
            String username,
            String email,
            String passwordHash,
            Role role,
            Organization organization
    ) {
        User user = switch (role) {
            case ADMIN -> new Admin(name, email, passwordHash);
            case COORDENADOR -> new Coordinator(name, email, passwordHash);
            case PROFESSOR -> new Teacher(name, email, passwordHash);
            case ALUNO -> new Student(name, email, passwordHash, UUID.randomUUID().toString());
        };
        user.setUsername(username);
        user.setOrganization(organization);
        user.setEnabled(true);
        user.setAccountNonLocked(true);
        return user;
    }
}
