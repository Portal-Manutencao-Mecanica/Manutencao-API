package com.weg.Maintenance_API.designation.entity;


import java.util.UUID;

import com.weg.Maintenance_API.enums.Sector;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Legacy entity. MaintenanceRequest now uses Sector directly.
@Entity
@Table(name = "designation")
@Getter
@Setter
@NoArgsConstructor
public class Designation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "designation_id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "number_card", nullable = false, unique = true, length = 255)
    private String numberCard = java.util.UUID.randomUUID().toString();

    @Enumerated(EnumType.STRING)
    @Column(name = "designation_sector")
    private Sector sector;
}
