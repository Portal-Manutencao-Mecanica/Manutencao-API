package com.weg.Maintenance_API.user.entity;

import com.weg.Maintenance_API.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
public abstract class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "user_name")
    private String name;
    @Column(name = "user_email")
    private String email;
    @Enumerated(EnumType.STRING)
    @Column(name = "role_user")
    private Role role;
    @Column(name = "user_created_at", nullable = false)
    private LocalDateTime createdAt;

    public User(String name, String email, Role role) {
        this.name = name;
        this.email = email;
        this.role = role;
        this.createdAt = LocalDateTime.now();
    }
}

