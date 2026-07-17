package com.weg.Manutencao_API.coordenador.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.weg.Manutencao_API.coordenador.entity.Coordinator;

@Repository
public interface CoordinatorRepository extends JpaRepository<Coordinator, Long> {
    
}
