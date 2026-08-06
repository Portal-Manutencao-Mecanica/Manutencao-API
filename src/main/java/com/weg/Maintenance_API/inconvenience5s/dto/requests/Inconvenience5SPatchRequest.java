package com.weg.Maintenance_API.inconvenience5s.dto.requests;

import com.weg.Maintenance_API.enums.RegistrationPeriod;
import com.weg.Maintenance_API.enums.Inconvenience5SStatus;
import com.weg.Maintenance_API.validation.enumValidator.ValidEnum;

// Executa a operacao deste metodo.
public record Inconvenience5SPatchRequest(
        String inconvenience,
        String description,
        @ValidEnum(message = "O periodo informado e invalido.", enumClass = RegistrationPeriod.class)
        String registrationPeriod,
        @ValidEnum(message = "A situacao informada e invalida.", enumClass = Inconvenience5SStatus.class)
        String status
) {
}
