package com.weg.Manutencao_API.maquina.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.weg.Manutencao_API.local.entity.Place;

import jakarta.annotation.Generated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
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

    @Column(name = "machine_place")
    private Place place;

    @Column(name = "machine_created_at")
    private LocalDateTime createdAt;

    @Column(name = "machine_log")
    private List<MachineLog> machineLog;

    public Machine(String name, String heritage, Place place, LocalDateTime createdAt, List<MachineLog> machineLog) {
        this.name = name;
        this.heritage = heritage;
        this.place = place;
        this.createdAt = createdAt;
        this.machineLog = machineLog;
    }
    
    

}
