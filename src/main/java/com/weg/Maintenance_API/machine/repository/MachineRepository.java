package com.weg.Maintenance_API.machine.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.weg.Maintenance_API.machine.entity.Machine;

@Repository
public interface MachineRepository extends JpaRepository<Machine, Long>{
    
    Page<Machine> findAll(Pageable pageable);
}


