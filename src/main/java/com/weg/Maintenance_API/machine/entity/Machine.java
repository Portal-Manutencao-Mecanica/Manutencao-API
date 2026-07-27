package com.weg.Maintenance_API.machine.entity;


import java.util.UUID;

import com.weg.Maintenance_API.enums.EquipmentCondition;
import com.weg.Maintenance_API.machinelog.entity.MachineLog;
import com.weg.Maintenance_API.place.entity.Place;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "machine")
@NoArgsConstructor
public class Machine {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "machine_id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "number_card", nullable = false, unique = true, length = 255)
    private String numberCard = java.util.UUID.randomUUID().toString();

    @Column(name = "machine_name", nullable = false)
    private String name;

    @Column(name = "machine_patrimony", nullable = false)
    private String patrimony;

    @Enumerated(EnumType.STRING)
    @Column(name = "machine_condition", nullable = false)
    private EquipmentCondition condition;

    @Column(name = "machine_tag")
    private String tag;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "machine", fetch = FetchType.LAZY)
    private List<MachineLog> machineLogs = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
