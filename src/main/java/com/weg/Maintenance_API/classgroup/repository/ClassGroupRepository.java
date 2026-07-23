package com.weg.Maintenance_API.classgroup.repository;


import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import com.weg.Maintenance_API.classgroup.entity.ClassGroup;

@Repository
public interface ClassGroupRepository extends JpaRepository<ClassGroup, UUID>{
    
    List<ClassGroup> findAllByEnabledTrue();
}


