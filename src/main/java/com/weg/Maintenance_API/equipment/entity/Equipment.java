package com.weg.Maintenance_API.equipment.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "equipment")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Equipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "equipment_id")
    private Long id;

    @Column(name = "equipment_name")
    private String name;

    @Column(name = "equipment_sap")
    private String sap;

    @Column(name = "equipment_price")
    private BigDecimal price;

    @Column(name = "equipment_quantity")
    private Integer quantity;

    public Equipment(String name, String sap, BigDecimal price, Integer quantity) {
        this.name = name;
        this.sap = sap;
        this.price = price;
        this.quantity = quantity;
    }

    

}

