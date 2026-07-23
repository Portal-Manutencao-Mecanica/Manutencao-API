package com.weg.Maintenance_API.teacher.repository;


import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import com.weg.Maintenance_API.teacher.entity.Teacher;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, UUID> {
    
    List<Teacher> findAllByEnabledTrue();
}


