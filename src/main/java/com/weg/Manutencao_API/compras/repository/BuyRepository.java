package com.weg.Manutencao_API.compras.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.weg.Manutencao_API.compras.entity.Buy;

@Repository
public interface BuyRepository extends JpaRepository<Buy, Long> {
    
}
