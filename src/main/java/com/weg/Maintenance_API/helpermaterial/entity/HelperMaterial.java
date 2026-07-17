package com.weg.Maintenance_API.helpermaterial.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "helper_material")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HelperMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "helper_material_id")
    private Long id;

    @Column(name = "helper_material_link_tecnic")
    private String technicalLink;

    @Column(name = "helper_material_link_lubrification")
    private String lubricationLink;

    @Column(name = "helper_material_link_prevent_maintance")
    private String preventiveMaintenanceLink;

    @Column(name = "helper_material_link_manual")
    private String linkManual;
}

