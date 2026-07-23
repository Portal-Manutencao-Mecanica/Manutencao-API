package com.weg.Maintenance_API.organization.repository;

import com.weg.Maintenance_API.organization.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrganizationRepository extends JpaRepository<Organization, UUID> {

    Optional<Organization> findByEmailDomainIgnoreCase(String emailDomain);

    boolean existsByNameIgnoreCase(String name);

    boolean existsByEmailDomainIgnoreCase(String emailDomain);

    boolean existsByNameIgnoreCaseAndIdNot(String name, UUID id);

    boolean existsByEmailDomainIgnoreCaseAndIdNot(String emailDomain, UUID id);
}
