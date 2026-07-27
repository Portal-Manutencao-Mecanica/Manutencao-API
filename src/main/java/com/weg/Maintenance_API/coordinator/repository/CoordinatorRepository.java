package com.weg.Maintenance_API.coordinator.repository;


import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.weg.Maintenance_API.coordinator.entity.Coordinator;

@Repository
public interface CoordinatorRepository extends JpaRepository<Coordinator, UUID> {
    
    Page<Coordinator> findAllByEnabledTrue(Pageable pageable);
}
