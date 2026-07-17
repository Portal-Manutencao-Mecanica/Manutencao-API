package com.weg.Manutencao_API.livrolog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.weg.Manutencao_API.livrolog.entity.MachineLog;

@Repository
public interface MachineLogRepository extends JpaRepository<MachineLog, Long> {
    
}
