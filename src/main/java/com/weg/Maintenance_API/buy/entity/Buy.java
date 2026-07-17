package com.weg.Maintenance_API.buy.entity;

import com.weg.Maintenance_API.student.entity.Student;
import com.weg.Maintenance_API.equipment.entity.Equipment;
import com.weg.Maintenance_API.enums.Status;
import com.weg.Maintenance_API.teacher.entity.Teacher;
import com.weg.Maintenance_API.classgroup.entity.ClassGroup;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "buy")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Buy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "buy_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "buy_status")
    private Status status;

    @JoinColumn(name = "buy_aluno_id")
    @ManyToOne
    private Student student;

    @JoinColumn(name = "buy_professor_id")
    @ManyToOne
    private Teacher teacher;

    @Column(name = "buy_technical_specification")
    private String technicalSpecification;

    @Column(name = "buy_quantity")
    private int quantity;

    @Column(name = "buy_sap")
    private String sap;

    @Column(name = "buy_purchase_justification")
    private String purchaseJustification;

    @ManyToOne
    @JoinColumn(name = "class_group_id")
    private ClassGroup classGroup;

    @Column(name = "buy_tag")
    private String tag;

    @Column(name = "buy_patrimony")
    private String patrimony;

    @ManyToOne
    @JoinColumn(name = "equipment_id")
    private Equipment equipment;

    @Column(name = "buy_mechanical_set")
    private String mechanicalSet;

    @Column(name = "buy_created_at")
    private LocalDate createdAt;

    @ElementCollection
    @CollectionTable(name = "buy_media_files", joinColumns = @JoinColumn(name = "buy_id"))
    @Column(name = "media_file")
    private List<String> mediaFiles = new ArrayList<>();
}


