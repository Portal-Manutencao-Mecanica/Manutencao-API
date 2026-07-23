package com.weg.Maintenance_API.coordinator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import com.weg.Maintenance_API.coordinator.entity.Coordinator;

@Repository
public interface CoordinatorRepository extends JpaRepository<Coordinator, Long> {
    
    List<Coordinator> findAllByEnabledTrue();
}


