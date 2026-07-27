package com.weg.Maintenance_API.classgroup.repository;


import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.weg.Maintenance_API.classgroup.entity.ClassGroup;

@Repository
public interface ClassGroupRepository extends JpaRepository<ClassGroup, UUID>{
    
    Page<ClassGroup> findAllByEnabledTrue(Pageable pageable);
}
