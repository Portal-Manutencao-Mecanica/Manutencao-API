package com.weg.Maintenance_API.buy.dto.request;

import com.weg.Maintenance_API.classgroup.entity.ClassGroup;
import com.weg.Maintenance_API.teacher.entity.Teacher;
import com.weg.Maintenance_API.validation.EntityExists;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record BuyDtoRequest(
        @NotBlank(message = "purchaseJustification can't be blank")
        String purchaseJustification,
        @NotNull(message = "classGroup can't be null")
        @EntityExists(entityClass = ClassGroup.class, message = "class group not found")
        Long classGroupId,
        @EntityExists(entityClass = Teacher.class, message = "teacher not found")
        Long notifiedTeacherId,
        @NotEmpty(message = "items can't be empty")
        List<BuyItemRequest> items,
        List<Long> mediaIds
) {
}
