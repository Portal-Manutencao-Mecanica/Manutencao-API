package com.weg.Manutencao_API.materialapoio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.weg.Manutencao_API.materialapoio.entity.HelperMaterial;

@Repository
public interface HelperMaterialRepository extends JpaRepository <HelperMaterial, Long>{
    
}
