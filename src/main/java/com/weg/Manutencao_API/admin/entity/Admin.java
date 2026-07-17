package com.weg.Manutencao_API.admin.entity;

import com.weg.Manutencao_API.enums.Role;
import com.weg.Manutencao_API.user.entity.User;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "admin")
public class Admin extends User {

    public Admin(String name, String email, Role role) {
        super(name, email, role);
    }
}
