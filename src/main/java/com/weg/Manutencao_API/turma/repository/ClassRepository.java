package com.weg.Manutencao_API.turma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.weg.Manutencao_API.turma.entity.ClassGroup;

@Repository
public interface ClassRepository extends JpaRepository<ClassGroup, Long>{
    
}
