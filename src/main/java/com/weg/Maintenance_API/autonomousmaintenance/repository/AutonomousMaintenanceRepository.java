package com.weg.Maintenance_API.autonomousmaintenance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.weg.Maintenance_API.autonomousmaintenance.entity.AutonomousMaintenance;

@Repository
public interface AutonomousMaintenanceRepository extends JpaRepository<AutonomousMaintenance, Long>{
    
}


