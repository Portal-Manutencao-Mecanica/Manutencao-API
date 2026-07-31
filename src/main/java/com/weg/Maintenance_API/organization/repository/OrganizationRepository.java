package com.weg.Maintenance_API.organization.repository;

import com.weg.Maintenance_API.organization.entity.Organization;
import com.weg.Maintenance_API.enums.OrganizationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

public interface OrganizationRepository extends JpaRepository<Organization, UUID> {

    Optional<Organization> findByEmailDomainIgnoreCase(String emailDomain);

    Optional<Organization> findByNameIgnoreCase(String name);

    Optional<Organization> findByType(OrganizationType type);

    List<Organization> findAllByType(OrganizationType type);

    boolean existsByNameIgnoreCase(String name);

    boolean existsByEmailDomainIgnoreCase(String emailDomain);

    boolean existsByNameIgnoreCaseAndIdNot(String name, UUID id);

    boolean existsByEmailDomainIgnoreCaseAndIdNot(String emailDomain, UUID id);
}
