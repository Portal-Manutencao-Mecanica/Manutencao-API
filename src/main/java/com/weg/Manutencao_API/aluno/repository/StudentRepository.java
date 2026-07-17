package com.weg.Manutencao_API.aluno.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.weg.Manutencao_API.aluno.entity.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    
}
