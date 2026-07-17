package com.weg.Manutencao_API.solicitacaomanutencao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaintenanceRepository extends JpaRepository<MaintenanceRepository, Long> {
    
}
