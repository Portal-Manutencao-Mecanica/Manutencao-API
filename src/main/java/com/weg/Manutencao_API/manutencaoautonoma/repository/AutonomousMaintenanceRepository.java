package com.weg.Manutencao_API.manutencaoautonoma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.weg.Manutencao_API.manutencaoautonoma.entity.AutonomousMaintenance;

@Repository
public interface AutonomousMaintenanceRepository extends JpaRepository<AutonomousMaintenance, Long>{
    
}
