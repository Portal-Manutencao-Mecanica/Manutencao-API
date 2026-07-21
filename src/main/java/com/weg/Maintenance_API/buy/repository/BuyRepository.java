package com.weg.Maintenance_API.buy.repository;

import com.weg.Maintenance_API.buy.entity.Buy;
import com.weg.Maintenance_API.enums.BuyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BuyRepository extends JpaRepository<Buy, Long> {

    List<Buy> findAllByStatus(BuyStatus status);
}