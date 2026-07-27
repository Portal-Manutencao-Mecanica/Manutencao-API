package com.weg.Maintenance_API.student.repository;


import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import com.weg.Maintenance_API.student.entity.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, UUID> {
    
    List<Student> findAllByEnabledTrue();
}


