package com.weg.Maintenance_API.machine.entity;

import com.weg.Maintenance_API.enums.EquipmentCondition;
import com.weg.Maintenance_API.machinelog.entity.MachineLog;
import com.weg.Maintenance_API.place.entity.Place;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "machine")
@AllArgsConstructor
@NoArgsConstructor
public class Machine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "machine_id")
    private Integer id;

    @Column(name = "machine_name", nullable = false)
    private String name;

    @Column(name = "machine_patrimony", nullable = false)
    private String patrimony;

    @Enumerated(EnumType.STRING)
    @Column(name = "machine_condition", nullable = false)
    private EquipmentCondition condition;

    @Column(name = "machine_tag")
    private String tag;

    @ManyToOne(optional = false)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;

    @Column(name = "machine_created_at", nullable = false)
    private LocalDate createdAt;

    @Column(name = "machine_last_maintenance")
    private LocalDateTime lastMaintenance;

    @OneToMany(mappedBy = "machine", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MachineLog> machineLogs = new ArrayList<>();
}


