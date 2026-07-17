package com.weg.Manutencao_API.equipamento.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.weg.Manutencao_API.equipamento.entity.Equipment;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
    
}
