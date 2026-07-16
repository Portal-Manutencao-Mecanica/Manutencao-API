package com.weg.Manutencao_API.livrolog.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.weg.Manutencao_API.maquina.entity.Machine;

import jakarta.annotation.Generated;
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
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "machine_log")
public class MachineLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "machine_log_id")
    private Long id;

    @Column(name = "machine_log_title")
    private String title;

    @Column(name = "machine_log_description")
    private String description;

    @Column(name = "machine_log_machine")
    private Machine machine;

    @Column(name = "machine_log_service_execute")
    private String serviceExecute;

    @Column(name = "machine_log_conclusion")
    private LocalDateTime conclusion;

    @Column(name = "machine_log_responsible")
    private Teacher teacher;
    
    @Column(name = "machine_log_hour_register")
    private LocalDateTime hourRegister;




}
