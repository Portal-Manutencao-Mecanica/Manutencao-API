package com.weg.Maintenance_API.classgroup.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.weg.Maintenance_API.classgroup.entity.ClassGroup;

@Repository
public interface ClassRepository extends JpaRepository<ClassGroup, Long>{
    
}


