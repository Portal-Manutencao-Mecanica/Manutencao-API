package com.weg.Maintenance_API.buy.dto.request;

import com.weg.Maintenance_API.enums.BuyStatus;
import com.weg.Maintenance_API.validation.enumValidator.ValidEnum;

// Executa a operacao deste metodo.
public record BuyPatchRequest(
        String purchaseJustification,
        @ValidEnum(message = "A situação informada é inválida.", enumClass = BuyStatus.class)
        String status
) {
}
