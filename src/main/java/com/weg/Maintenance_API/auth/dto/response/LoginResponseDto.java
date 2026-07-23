package com.weg.Maintenance_API.auth.dto.response;

import com.weg.Maintenance_API.user.dto.response.UserResponseDto;

public record LoginResponseDto(
        String accessToken,
        String tokenType,
        long expiresIn,
        UserResponseDto user
) {
}
