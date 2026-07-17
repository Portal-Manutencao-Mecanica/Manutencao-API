package com.weg.Maintenance_API.helpermaterial.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.weg.Maintenance_API.helpermaterial.entity.HelperMaterial;

@Repository
public interface HelperMaterialRepository extends JpaRepository <HelperMaterial, Long>{
    
}


