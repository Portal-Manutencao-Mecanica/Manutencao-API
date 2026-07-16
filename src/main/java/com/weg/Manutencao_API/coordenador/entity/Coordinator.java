package com.weg.Manutencao_API.coordenador.entity;

import com.weg.Manutencao_API.enums.Role;
import com.weg.Manutencao_API.usuario.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "coordinator")
public class Coordinator extends User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coordinator_id")
    private Long id;

    public Coordinator(String name, String email, Role role) {
        super(name, email, role);
    }
}