package com.weg.Maintenance_API.buy.repository;


import java.util.UUID;

import com.weg.Maintenance_API.buy.entity.Buy;
import com.weg.Maintenance_API.enums.BuyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface BuyRepository extends JpaRepository<Buy, UUID> {

    Page<Buy> findAllByStatus(BuyStatus status, Pageable pageable);
}
