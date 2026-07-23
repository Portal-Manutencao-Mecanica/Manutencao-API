package com.weg.Maintenance_API.organization.service;

import com.weg.Maintenance_API.exception.type.ConflictException;
import com.weg.Maintenance_API.exception.type.ResourceNotFoundException;
import com.weg.Maintenance_API.organization.dto.CreateOrganizationRequest;
import com.weg.Maintenance_API.organization.dto.OrganizationResponse;
import com.weg.Maintenance_API.organization.dto.UpdateOrganizationRequest;
import com.weg.Maintenance_API.organization.entity.Organization;
import com.weg.Maintenance_API.organization.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationRepository organizationRepository;

    @Transactional
    public OrganizationResponse create(CreateOrganizationRequest request) {
        String domain = normalizeDomain(request.emailDomain());
        validateUnique(request.name(), domain, null);

        Organization organization = new Organization(request.name().trim(), request.type(), domain);
        return toResponse(organizationRepository.save(organization));
    }

    @Transactional(readOnly = true)
    public Page<OrganizationResponse> findAll(Pageable pageable) {
        return organizationRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public OrganizationResponse findById(UUID id) {
        return toResponse(getRequired(id));
    }

    @Transactional
    public OrganizationResponse update(UUID id, UpdateOrganizationRequest request) {
        Organization organization = getRequired(id);
        String name = request.name() == null ? organization.getName() : request.name().trim();
        String domain = request.emailDomain() == null
                ? organization.getEmailDomain()
                : normalizeDomain(request.emailDomain());
        validateUnique(name, domain, id);

        organization.setName(name);
        organization.setEmailDomain(domain);
        if (request.type() != null) {
            organization.setType(request.type());
        }
        return toResponse(organizationRepository.save(organization));
    }

    @Transactional
    public OrganizationResponse setActive(UUID id, boolean active) {
        Organization organization = getRequired(id);
        organization.setActive(active);
        return toResponse(organizationRepository.save(organization));
    }

    public Organization getRequired(UUID id) {
        return organizationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organização", id));
    }

    private void validateUnique(String name, String domain, UUID currentId) {
        boolean duplicatedName = currentId == null
                ? organizationRepository.existsByNameIgnoreCase(name)
                : organizationRepository.existsByNameIgnoreCaseAndIdNot(name, currentId);
        if (duplicatedName) {
            throw new ConflictException("Já existe uma organização com este nome.");
        }

        boolean duplicatedDomain = currentId == null
                ? organizationRepository.existsByEmailDomainIgnoreCase(domain)
                : organizationRepository.existsByEmailDomainIgnoreCaseAndIdNot(domain, currentId);
        if (duplicatedDomain) {
            throw new ConflictException("Já existe uma organização com este domínio de e-mail.");
        }
    }

    private OrganizationResponse toResponse(Organization organization) {
        return new OrganizationResponse(
                organization.getId(),
                organization.getName(),
                organization.getType(),
                organization.getEmailDomain(),
                organization.isActive(),
                organization.getCreatedAt(),
                organization.getUpdatedAt()
        );
    }

    private String normalizeDomain(String domain) {
        return domain.trim().toLowerCase(Locale.ROOT).replaceFirst("^@", "");
    }
}
