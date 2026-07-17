package com.weg.Manutencao_API.manutencaoautonoma.entity;

import com.weg.Manutencao_API.aluno.entity.Student;
import com.weg.Manutencao_API.enums.EquipmentCondition;
import com.weg.Manutencao_API.enums.EquipmentSituation;
import com.weg.Manutencao_API.equipamento.entity.Equipment;
import com.weg.Manutencao_API.professor.entity.Teacher;
import jakarta.persistence.Column;
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

import java.time.LocalDateTime;

@Entity
@Table(name = "autonomous_maintenance")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AutonomousMaintenance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "autonomous_maintenance_id")
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "equipment_situation", nullable = false)
    private EquipmentSituation equipmentSituation;

    @Column(name = "date_time", nullable = false)
    private LocalDateTime dateTime;

    @ManyToOne(optional = false)
    @JoinColumn(name = "equipment_id", nullable = false)
    private Equipment inspectedEquipment;

    @Enumerated(EnumType.STRING)
    @Column(name = "equipment_condition", nullable = false)
    private EquipmentCondition equipmentCondition;

    @Column(name = "identified_nonconformities", columnDefinition = "TEXT")
    private String identifiedNonconformities;

    @Column(name = "patrimony")
    private String patrimony;

    @Column(name = "tag")
    private String tag;

    @ManyToOne(optional = false)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    @ManyToOne(optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
}
