package com.weg.Maintenance_API.helpermaterial.entity;

import com.weg.Maintenance_API.enums.HelperMaterialType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "helper_material")
@Getter
@Setter
@NoArgsConstructor
public class HelperMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "helper_material_id")
    private Long id;

    @Column(name = "number_card", nullable = false, unique = true, length = 255)
    private String numberCard = java.util.UUID.randomUUID().toString();

    @Column(name = "title", nullable = false, length = 150)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "url", nullable = false, length = 2048)
    private String url;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 40)
    private HelperMaterialType type;
}
