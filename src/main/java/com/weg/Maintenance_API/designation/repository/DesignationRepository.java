package com.weg.Maintenance_API.designation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.weg.Maintenance_API.designation.entity.Designation;

@Repository
public interface DesignationRepository extends JpaRepository<Designation, Long> {

    
}


