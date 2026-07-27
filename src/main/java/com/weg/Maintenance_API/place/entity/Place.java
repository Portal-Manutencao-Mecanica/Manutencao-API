package com.weg.Maintenance_API.place.entity;


import java.util.UUID;

import java.util.ArrayList;
import java.util.List;

import com.weg.Maintenance_API.machine.entity.Machine;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "place")
@Getter
@Setter
@NoArgsConstructor
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "place_id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "number_card", nullable = false, unique = true, length = 255)
    private String numberCard = java.util.UUID.randomUUID().toString();

    @Column(name = "place_name", nullable = false, unique = true, length = 120)
    private String name;

    @OneToMany(mappedBy = "place", fetch = FetchType.LAZY)
    private List<Machine> machines = new ArrayList<>();

}
