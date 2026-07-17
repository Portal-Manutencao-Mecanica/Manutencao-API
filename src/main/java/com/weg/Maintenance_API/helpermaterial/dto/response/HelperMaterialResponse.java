package com.weg.Maintenance_API.helpermaterial.dto.response;

public record HelperMaterialResponse(
        Long id,
        String technicalLink,
        String lubricationLink,
        String preventiveMaintenanceLink,
        String linkManual) {

}


