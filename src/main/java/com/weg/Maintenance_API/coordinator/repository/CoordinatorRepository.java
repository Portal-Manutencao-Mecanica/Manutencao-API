package com.weg.Maintenance_API.coordinator.repository;

import com.weg.Maintenance_API.coordinator.entity.Coordinator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CoordinatorRepository extends JpaRepository<Coordinator, UUID> {

    Page<Coordinator> findAllByEnabledTrue(Pageable pageable);

    List<Coordinator> findAllByOrganizationIdAndEnabledTrueAndAccountNonLockedTrue(UUID organizationId);
}
