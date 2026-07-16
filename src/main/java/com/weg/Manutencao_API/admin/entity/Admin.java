package com.weg.Manutencao_API.admin.entity;

import com.weg.Manutencao_API.enums.Role;
import com.weg.Manutencao_API.usuario.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Admin extends User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id")
    private Long id;

    public Admin(String name, String email, Role role) {
        super(name, email, role);
    }
}
