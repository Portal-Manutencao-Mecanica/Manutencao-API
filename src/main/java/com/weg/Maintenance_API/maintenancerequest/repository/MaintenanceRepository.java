package com.weg.Maintenance_API.maintenancerequest.repository;


import java.util.UUID;

import com.weg.Maintenance_API.maintenancerequest.entity.MaintenanceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaintenanceRepository extends JpaRepository<MaintenanceRequest, UUID> {
    
}


