package com.weg.Maintenance_API.buy.entity;

import com.weg.Maintenance_API.equipment.entity.Equipment;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "buy_item")
@Getter
@Setter
@NoArgsConstructor
public class BuyItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "buy_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "buy_id", nullable = false)
    private Buy buy;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "equipment_id", nullable = false)
    private Equipment equipment;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "technical_specification", columnDefinition = "TEXT")
    private String technicalSpecification;

    @Column(name = "sap", length = 100)
    private String sap;

    @Column(name = "patrimony", length = 100)
    private String patrimony;

    @Column(name = "tag", length = 100)
    private String tag;

    @Column(name = "mechanical_set", length = 150)
    private String mechanicalSet;
}
