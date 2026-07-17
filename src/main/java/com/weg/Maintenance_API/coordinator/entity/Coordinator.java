package com.weg.Maintenance_API.coordinator.entity;

import com.weg.Maintenance_API.enums.Role;
import com.weg.Maintenance_API.user.entity.User;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "coordinator")
public class Coordinator extends User {
    
    public Coordinator(String name, String email, Role role) {
        super(name, email, role);
    }
}


