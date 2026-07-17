package com.weg.Manutencao_API.compras.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record BuyDtoRequest(

        @NotNull(message = "status can't be null")
        String status,
        @NotNull(message = "student can't be null")
        Long studentId,
        @NotNull(message = "teacher can't be null")
        Long teacherId,
        String technicalSpecification,
        @NotNull(message = "quantity can't be null")
        int quantity,
        String sap,
        @NotNull(message = "purchaseJustification can't be null")
        String purchaseJustification,
        @NotNull(message = "classGroup can't be null")
        Long classGroupId,
        String tag,
        @NotNull(message = "patrimony can't be null")
        String patrimony,
        @NotNull(message = "equipament can't be null")
        Long equipamentId,
        @NotNull(message = "mechanicalSet can't be null")
        String mechanicalSet,
        List<String> media
        ) {
}
