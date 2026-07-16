package com.weg.Manutencao_API.local.entity;

import java.util.List;
import java.util.ArrayList;

import com.weg.Manutencao_API.maquina.entity.Machine;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "place")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "place_id")
    private Long id;

    @Column(name = "place_name")
    private String name;

    @OneToMany(mappedBy = "place")
    private List<Machine> machines = new ArrayList<>();

    public Place(String name, List<Machine> machines) {
        this.name = name;
        this.machines = machines;
    }
    
}
