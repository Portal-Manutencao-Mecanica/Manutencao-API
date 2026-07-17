package com.weg.Manutencao_API.designacao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.weg.Manutencao_API.designacao.entity.Designation;

@Repository
public interface DesignationRepository extends JpaRepository<Designation, Long> {

    
}
