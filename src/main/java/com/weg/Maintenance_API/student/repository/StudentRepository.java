package com.weg.Maintenance_API.student.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.weg.Maintenance_API.student.entity.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    
}


