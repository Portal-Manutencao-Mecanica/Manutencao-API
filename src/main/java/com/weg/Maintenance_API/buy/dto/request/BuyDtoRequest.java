package com.weg.Maintenance_API.buy.dto.request;


import java.util.UUID;

import com.weg.Maintenance_API.classgroup.entity.ClassGroup;
import com.weg.Maintenance_API.teacher.entity.Teacher;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

// Executa a operacao deste metodo.
public record BuyDtoRequest(
        @NotBlank(message = "purchaseJustification can't be blank")
        String purchaseJustification,
        @NotNull(message = "classGroup can't be null")
UUID classGroupId,
UUID notifiedTeacherId,
        @NotEmpty(message = "items can't be empty")
        List<BuyItemRequest> items,
        List<UUID> mediaIds
) {
}
