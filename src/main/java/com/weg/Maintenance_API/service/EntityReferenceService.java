package com.weg.Maintenance_API.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import com.weg.Maintenance_API.classgroup.entity.ClassGroup;
import com.weg.Maintenance_API.classgroup.repository.ClassGroupRepository;
import com.weg.Maintenance_API.equipment.entity.Equipment;
import com.weg.Maintenance_API.equipment.repository.EquipmentRepository;
import com.weg.Maintenance_API.exception.type.ResourceNotFoundException;
import com.weg.Maintenance_API.machine.entity.Machine;
import com.weg.Maintenance_API.machine.repository.MachineRepository;
import com.weg.Maintenance_API.place.entity.Place;
import com.weg.Maintenance_API.place.repository.PlaceRepository;
import com.weg.Maintenance_API.student.entity.Student;
import com.weg.Maintenance_API.student.repository.StudentRepository;
import com.weg.Maintenance_API.teacher.entity.Teacher;
import com.weg.Maintenance_API.teacher.repository.TeacherRepository;

@Service
@RequiredArgsConstructor
public class EntityReferenceService {
    private final ClassGroupRepository classGroupRepository;
    private final EquipmentRepository equipmentRepository;
    private final MachineRepository machineRepository;
    private final PlaceRepository placeRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    // Executa a operacao deste metodo.
    public ClassGroup classGroup(UUID id) {
        return classGroupRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Turma", id));
    }

    // Executa a operacao deste metodo.
    public Equipment equipment(UUID id) {
        return equipmentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Equipamento", id));
    }

    // Executa a operacao deste metodo.
    public Machine machine(UUID id) {
        return machineRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Maquina", id));
    }

    // Executa a operacao deste metodo.
    public Place place(UUID id) {
        return placeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Lugar", id));
    }

    // Executa a operacao deste metodo.
    public Student student(UUID id) {
        return studentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Aluno", id));
    }

    // Executa a operacao deste metodo.
    public Teacher teacher(UUID id) {
        return teacherRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Professor", id));
    }

    // Executa a operacao deste metodo.
    public List<Student> students(List<UUID> ids) {
        List<Student> students = new ArrayList<>();
        for (UUID id : ids) {
            students.add(student(id));
        }
        return students;
    }
}
