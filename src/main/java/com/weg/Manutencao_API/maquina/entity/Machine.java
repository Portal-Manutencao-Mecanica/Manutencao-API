package com.weg.Manutencao_API.maquina.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.weg.Manutencao_API.local.entity.Place;
import com.weg.Manutencao_API.livrolog.entity.MachineLog;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "machine")
@AllArgsConstructor
@NoArgsConstructor
public class Machine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "machine_id")
    private Long id;

    @Column(name = "machine_name")
    private String name;

    @Column(name = "machine_heritage")
    private String heritage;

    @ManyToOne
    @JoinColumn(name = "place_id")
    private Place place;

    @Column(name = "machine_created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "machine")
    private List<MachineLog> machineLog = new ArrayList<>();

    public Machine(String name, String heritage, Place place, LocalDateTime createdAt, List<MachineLog> machineLog) {
        this.name = name;
        this.heritage = heritage;
        this.place = place;
        this.createdAt = createdAt;
        this.machineLog = machineLog;
    }
    
    

}
