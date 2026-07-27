package com.weg.Maintenance_API.user.mapper;

import com.weg.Maintenance_API.organization.dto.OrganizationSummaryResponse;
import com.weg.Maintenance_API.user.dto.response.UserResponseDto;
import com.weg.Maintenance_API.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserResponseMapper {

    public UserResponseDto toResponse(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getStatus(),
                user.isPasswordChangeRequired(),
                new OrganizationSummaryResponse(
                        user.getOrganization().getId(),
                        user.getOrganization().getName()
                ),
                user.getNumberCard(),
                user.isEnabled(),
                user.isAccountNonLocked(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
