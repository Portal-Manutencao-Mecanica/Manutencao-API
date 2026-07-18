package com.weg.Maintenance_API.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreateRequestDto(
        @NotBlank(message = "name can't be blank")
        @Size(min = 3, max = 120)
        String name,

        @NotBlank(message = "email can't be blank")
        @Email(message = "email must be valid")
        @Size(max = 150)
        String email,

        @NotBlank(message = "password can't be blank")
        @Size(min = 8, max = 72)
        String password
) {
}
