package com.weg.Maintenance_API.equipment.repository;


import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.weg.Maintenance_API.equipment.entity.Equipment;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, UUID> {
    
}


