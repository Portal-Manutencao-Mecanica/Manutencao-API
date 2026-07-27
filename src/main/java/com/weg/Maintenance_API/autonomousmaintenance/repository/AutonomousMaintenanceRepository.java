package com.weg.Maintenance_API.autonomousmaintenance.repository;


import java.util.UUID;

import com.weg.Maintenance_API.autonomousmaintenance.entity.AutonomousMaintenance;
import com.weg.Maintenance_API.enums.EquipmentSituation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface AutonomousMaintenanceRepository extends JpaRepository<AutonomousMaintenance, UUID> {
    Page<AutonomousMaintenance> findAllByEquipmentSituation(
            EquipmentSituation equipmentSituation,
            Pageable pageable
    );
}
