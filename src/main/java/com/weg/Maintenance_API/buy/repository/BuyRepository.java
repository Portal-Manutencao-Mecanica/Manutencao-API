package com.weg.Maintenance_API.buy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.weg.Maintenance_API.buy.entity.Buy;

@Repository
public interface BuyRepository extends JpaRepository<Buy, Long> {
    
}


