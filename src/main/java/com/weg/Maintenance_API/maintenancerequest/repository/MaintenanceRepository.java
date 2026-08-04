package com.weg.Maintenance_API.maintenancerequest.repository;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

import com.weg.Maintenance_API.maintenancerequest.entity.MaintenanceRequest;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MaintenanceRepository extends JpaRepository<MaintenanceRequest, UUID>, JpaSpecificationExecutor<MaintenanceRequest> {

    List<MaintenanceRequest> findAllByCreatedById(UUID userId);

    List<MaintenanceRequest> findAllByNotifiedTeacherId(UUID teacherId);

    List<MaintenanceRequest> findAllByWorkOrderNumberIsNotNull();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select request from MaintenanceRequest request where request.id = :id")
    Optional<MaintenanceRequest> findByIdForUpdate(@Param("id") UUID id);
}
