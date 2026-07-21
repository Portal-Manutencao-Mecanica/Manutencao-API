package com.weg.Maintenance_API.buy.repository;

import com.weg.Maintenance_API.buy.dto.response.BuyDtoResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.weg.Maintenance_API.buy.entity.Buy;

import java.util.List;
import java.util.Optional;

@Repository
public interface BuyRepository extends JpaRepository<Buy, Long> {
    Optional<List<BuyDtoResponse>> findAllByStatus(String status);
}


