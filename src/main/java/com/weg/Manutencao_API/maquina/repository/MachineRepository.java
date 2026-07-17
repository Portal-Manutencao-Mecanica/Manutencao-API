package com.weg.Manutencao_API.maquina.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.weg.Manutencao_API.maquina.entity.Machine;

@Repository
public interface MachineRepository extends JpaRepository<Machine, Long>{
    
}
