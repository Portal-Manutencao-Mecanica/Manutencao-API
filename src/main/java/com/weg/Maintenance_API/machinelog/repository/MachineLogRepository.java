package com.weg.Maintenance_API.machinelog.repository;


import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.weg.Maintenance_API.machinelog.entity.MachineLog;

@Repository
public interface MachineLogRepository extends JpaRepository<MachineLog, UUID> {
    
}


