
package com.weg.Maintenance_API.notification.dto.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NotificationRequest(

        @NotBlank
        @Email
        @Size(max = 150)
        String email,

        @NotBlank
        @Size(max = 150)
        String title,

        @Size(max = 255)
        String about,

        @NotBlank
        String description
) {
}