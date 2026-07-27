package com.weg.Maintenance_API.place.repository;


import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.weg.Maintenance_API.place.entity.Place;

@Repository
public interface PlaceRepository extends JpaRepository<Place, UUID>{
    
}


