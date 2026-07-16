package com.weg.Manutencao_API.equipamento.entity;

import java.math.BigDecimal;

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
@Table(name = "equipment")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Equipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "equipment_id")
    private Long id;

    @Column(name = "equipment_name")
    private String name;

    @Column(name = "equipement_sap")
    private String sap;

    @Column(name = "equipement_price")
    private BigDecimal price;

    @Column(name = "equipement_quantity")
    private int quantity;

    public Equipment(String name, String sap, BigDecimal price, int quantity) {
        this.name = name;
        this.sap = sap;
        this.price = price;
        this.quantity = quantity;
    }

    

}
