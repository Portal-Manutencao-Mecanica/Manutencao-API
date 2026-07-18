package com.weg.Maintenance_API.helpermaterial.dto.request;

import com.weg.Maintenance_API.enums.HelperMaterialType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.URL;

public record HelperMaterialRequest(
    @NotBlank(message = "title can't be blank")
    String title,
    String description,
    @URL(message = "url must be a valid URL")
    @NotBlank(message = "url can't be blank")
    String url,
    @NotNull(message = "type can't be null")
    HelperMaterialType type
) {
}

