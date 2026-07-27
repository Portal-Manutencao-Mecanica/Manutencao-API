package com.weg.Maintenance_API.auth.dto.response;

import com.weg.Maintenance_API.user.dto.response.UserResponseDto;

// Executa a operacao deste metodo.
public record LoginResponseDto(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn,
        boolean passwordChangeRequired,
        UserResponseDto user
) {
}
