package com.weg.Maintenance_API.place.entity;

import java.util.ArrayList;
import java.util.List;

import com.weg.Maintenance_API.machine.entity.Machine;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "place")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "place_id")
    private Long id;

    @Column(name = "place_name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "place", fetch = FetchType.LAZY)
    private List<Machine> machines = new ArrayList<>();

    public Place(String name, List<Machine> machines) {
        this.name = name;
        this.machines = machines;
    }
    
}

