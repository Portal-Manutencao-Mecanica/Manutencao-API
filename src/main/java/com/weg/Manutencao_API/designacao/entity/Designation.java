package com.weg.Manutencao_API.designacao.entity;

import com.weg.Manutencao_API.enums.Sector;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "designation")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Designation{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "designation_id")
    private Long id;
    @Column(name = "designation_sector")
    private Sector sector;
}
