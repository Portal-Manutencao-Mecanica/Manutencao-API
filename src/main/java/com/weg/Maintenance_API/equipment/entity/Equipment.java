package com.weg.Maintenance_API.equipment.entity;


import java.util.UUID;

import com.weg.Maintenance_API.media.entity.Media;
import jakarta.persistence.CascadeType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "equipment")
@Getter
@Setter
@NoArgsConstructor
public class Equipment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "equipment_id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "number_card", nullable = false, unique = true, length = 255)
    private String numberCard = java.util.UUID.randomUUID().toString();

    @Column(name = "equipment_name", nullable = false, length = 150)
    private String name;

    @Column(name = "equipment_sap", unique = true, length = 100)
    private String sap;

    @Column(name = "unit_price", precision = 15, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "available_quantity", nullable = false)
    private Integer availableQuantity;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(
            name = "equipment_media",
            joinColumns = @JoinColumn(name = "equipment_id"),
            inverseJoinColumns = @JoinColumn(name = "media_id")
    )
    private List<Media> media = new ArrayList<>();

    public Equipment(String name, String sap, BigDecimal unitPrice, Integer availableQuantity) {
        this.name = name;
        this.sap = sap;
        this.unitPrice = unitPrice;
        this.availableQuantity = availableQuantity;
    }
}
