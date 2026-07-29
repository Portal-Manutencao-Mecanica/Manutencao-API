package com.weg.Maintenance_API.machine.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.weg.Maintenance_API.machine.entity.Machine;

import java.util.UUID;

@Repository
public interface MachineRepository extends JpaRepository<Machine, UUID>{
    
    Page<Machine> findAll(Pageable pageable);
}
