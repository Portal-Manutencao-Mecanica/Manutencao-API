package com.weg.Manutencao_API.local.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.weg.Manutencao_API.local.entity.Place;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long>{
    
}
