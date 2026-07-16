package com.weg.Manutencao_API.livrolog.entity;

import java.time.LocalDateTime;

import com.weg.Manutencao_API.maquina.entity.Machine;
import com.weg.Manutencao_API.professor.entity.Teacher;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

    @ManyToOne(optional = false)
    @JoinColumn(name = "machine_id", nullable = false)
    private Machine machine;

    @Column(name = "machine_log_service_execute")
    private String serviceExecute;

    @Column(name = "machine_log_conclusion")
    private LocalDateTime conclusion;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;
    
    @Column(name = "machine_log_hour_register")
    private LocalDateTime hourRegister;




}
