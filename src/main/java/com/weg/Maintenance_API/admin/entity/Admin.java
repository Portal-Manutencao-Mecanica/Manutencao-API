package com.weg.Maintenance_API.admin.entity;

import com.weg.Maintenance_API.enums.Role;
import com.weg.Maintenance_API.user.entity.User;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "admin")
public class Admin extends User {

    public Admin() {
        setRole(Role.ADMIN);
    }

    public Admin(String name, String email, String password) {
        super(name, email, password, Role.ADMIN);
    }
}
