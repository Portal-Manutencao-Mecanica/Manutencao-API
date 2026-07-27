package com.weg.Maintenance_API.userimport.service;

import com.weg.Maintenance_API.enums.Role;
import com.weg.Maintenance_API.organization.entity.Organization;
import com.weg.Maintenance_API.user.UserRepository;
import com.weg.Maintenance_API.user.entity.User;
import com.weg.Maintenance_API.userimport.dto.UserImportItemResponse;
import com.weg.Maintenance_API.userimport.dto.UserImportResponse;
import com.weg.Maintenance_API.userimport.entity.UserImport;
import com.weg.Maintenance_API.userimport.entity.UserImportItem;
import com.weg.Maintenance_API.userimport.repository.UserImportItemRepository;
import com.weg.Maintenance_API.userimport.repository.UserImportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserImportLifecycleService {

    private final UserImportRepository userImportRepository;
    private final UserImportItemRepository itemRepository;
    private final UserRepository userRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public UUID start(
            String filename,
            UUID actorId,
            int totalRows
    ) {
        User actor = userRepository.findById(actorId).orElseThrow();
        Organization organization = actor.getRole() == Role.COORDENADOR
                ? actor.getOrganization()
                : null;
        return userImportRepository.save(
                new UserImport(filename, actor, organization, totalRows)
        ).getId();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void complete(UUID importId, int created, int failed) {
        UserImport userImport = userImportRepository.findById(importId).orElseThrow();
        userImport.complete(created, failed);
    }

    @Transactional(readOnly = true)
    public UserImportResponse response(UUID importId) {
        UserImport userImport = userImportRepository.findById(importId).orElseThrow();
        List<UserImportItemResponse> items = itemRepository
                .findAllByUserImportIdOrderByRowNumber(importId)
                .stream()
                .map(this::toResponse)
                .toList();
        return new UserImportResponse(
                userImport.getId(),
                userImport.getFilename(),
                userImport.getTotalRows(),
                userImport.getCreatedCount(),
                userImport.getFailedCount(),
                userImport.getStatus(),
                userImport.getCreatedAt(),
                userImport.getCompletedAt(),
                items
        );
    }

    private UserImportItemResponse toResponse(UserImportItem item) {
        return new UserImportItemResponse(
                item.getId(),
                item.getRowNumber(),
                item.getName(),
                item.getUsername(),
                item.getEmail(),
                item.getRole(),
                item.getOrganizationValue(),
                item.getStatus(),
                item.getCreatedUser() == null ? null : item.getCreatedUser().getId(),
                item.getErrorCode(),
                item.getErrorField(),
                item.getErrorMessage()
        );
    }
}
