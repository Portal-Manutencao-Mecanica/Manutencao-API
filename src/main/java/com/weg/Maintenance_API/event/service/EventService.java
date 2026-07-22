package com.weg.Maintenance_API.event.service;

import com.weg.Maintenance_API.equipment.entity.Equipment;
import com.weg.Maintenance_API.equipment.repository.EquipmentRepository;
import com.weg.Maintenance_API.event.dto.request.CalendarCreateRequestDto;
import com.weg.Maintenance_API.event.dto.request.CalendarUpdateRequestDto;
import com.weg.Maintenance_API.event.dto.response.CalendarResponseDto;
import com.weg.Maintenance_API.event.entity.Event;
import com.weg.Maintenance_API.event.mapper.EventMapper;
import com.weg.Maintenance_API.event.repository.EventRepository;
import com.weg.Maintenance_API.machine.entity.Machine;
import com.weg.Maintenance_API.machine.repository.MachineRepository;
import com.weg.Maintenance_API.place.entity.Place;
import com.weg.Maintenance_API.place.repository.PlaceRepository;
import com.weg.Maintenance_API.student.entity.Student;
import com.weg.Maintenance_API.student.repository.StudentRepository;
import com.weg.Maintenance_API.teacher.entity.Teacher;
import com.weg.Maintenance_API.teacher.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final EquipmentRepository equipmentRepository;
    private final MachineRepository machineRepository;
    private final PlaceRepository placeRepository;

    @Transactional
    public CalendarResponseDto create(CalendarCreateRequestDto request) {
        Event event = new Event();
        applyCreateFields(event, request);
        return eventMapper.toResponse(eventRepository.save(event));
    }

    @Transactional(readOnly = true)
    public List<CalendarResponseDto> getAll() {
        return eventRepository.findAll().stream().map(eventMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public CalendarResponseDto getById(Long id) {
        return eventMapper.toResponse(findById(id));
    }

    @Transactional
    public CalendarResponseDto update(Long id, CalendarUpdateRequestDto request) {
        Event event = findById(id);
        if (request.scheduledAction() != null) event.setScheduledAction(request.scheduledAction());
        if (request.criticality() != null) event.setCriticality(request.criticality());
        if (request.scheduledFor() != null) event.setScheduledFor(request.scheduledFor());
        if (request.requestedAt() != null) event.setRequestedAt(request.requestedAt());
        if (request.studentId() != null) event.setStudent(findStudent(request.studentId()));
        if (request.teacherId() != null) event.setTeacher(findTeacher(request.teacherId()));
        if (request.equipmentId() != null) event.setEquipment(findEquipment(request.equipmentId()));
        if (request.machineId() != null) event.setMachine(findMachine(request.machineId()));
        if (request.placeId() != null) event.setPlace(findPlace(request.placeId()));
        if (request.maintenanceType() != null) event.setMaintenanceType(request.maintenanceType());
        if (request.status() != null) event.setStatus(request.status());
        return eventMapper.toResponse(eventRepository.save(event));
    }

    @Transactional
    public void delete(Long id) {
        eventRepository.delete(findById(id));
    }

    private void applyCreateFields(Event event, CalendarCreateRequestDto request) {
        event.setScheduledAction(request.scheduledAction());
        event.setCriticality(request.criticality());
        event.setScheduledFor(request.scheduledFor());
        event.setRequestedAt(request.requestedAt());
        event.setStudent(request.studentId() == null ? null : findStudent(request.studentId()));
        event.setTeacher(findTeacher(request.teacherId()));
        event.setEquipment(findEquipment(request.equipmentId()));
        event.setMachine(findMachine(request.machineId()));
        event.setPlace(findPlace(request.placeId()));
        event.setMaintenanceType(request.maintenanceType());
        if (request.status() != null) event.setStatus(request.status());
    }

    private Event findById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
    }

    private Student findStudent(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));
    }

    private Teacher findTeacher(Long id) {
        return teacherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
    }

    private Equipment findEquipment(Long id) {
        return equipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipment not found"));
    }

    private Machine findMachine(Long id) {
        return machineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Machine not found"));
    }

    private Place findPlace(Long id) {
        return placeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Place not found"));
    }
}