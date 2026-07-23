package com.weg.Maintenance_API.autonomousmaintenance.repository;

import com.weg.Maintenance_API.autonomousmaintenance.entity.AutonomousMaintenance;
import com.weg.Maintenance_API.enums.EquipmentSituation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AutonomousMaintenanceRepository extends JpaRepository<AutonomousMaintenance, Long> {
    List<AutonomousMaintenance> findAllByEquipmentSituation(EquipmentSituation equipmentSituation);
}