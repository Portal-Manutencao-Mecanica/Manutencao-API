package com.weg.Maintenance_API.inconvenience5s.dto.requests;

import com.weg.Maintenance_API.enums.RegistrationPeriod;

public record Inconvenience5SPatchRequest(
        String inconvenience,
        String description,
        RegistrationPeriod registrationPeriod
) {
}