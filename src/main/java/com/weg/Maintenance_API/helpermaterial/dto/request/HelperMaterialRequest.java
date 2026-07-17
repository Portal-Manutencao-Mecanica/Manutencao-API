package com.weg.Maintenance_API.helpermaterial.dto.request;

import org.hibernate.validator.constraints.URL;

public record HelperMaterialRequest(
    @URL(message = "technical link must be a valid URL")
    String technicalLink,
    @URL(message = "lubrication link must be a valid URL")
    String lubricationLink,
    @URL(message = "preventive maintenance link must be a valid URL")
    String preventiveMaintenanceLink,
    @URL(message = "manual link must be a valid URL")
    String linkManual
) {
}



