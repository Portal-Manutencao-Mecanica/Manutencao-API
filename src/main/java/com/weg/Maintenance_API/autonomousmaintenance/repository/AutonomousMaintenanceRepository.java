package com.weg.Maintenance_API.autonomousmaintenance.repository;

import com.weg.Maintenance_API.autonomousmaintenance.entity.AutonomousMaintenance;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AutonomousMaintenanceRepository extends
        JpaRepository<AutonomousMaintenance, UUID>,
        JpaSpecificationExecutor<AutonomousMaintenance> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select maintenance from AutonomousMaintenance maintenance where maintenance.id = :id")
    Optional<AutonomousMaintenance> findByIdForUpdate(@Param("id") UUID id);
}
