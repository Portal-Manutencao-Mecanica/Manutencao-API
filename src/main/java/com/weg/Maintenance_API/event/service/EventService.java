package com.weg.Maintenance_API.event.service;


import java.util.UUID;
import java.util.Locale;

import com.weg.Maintenance_API.enums.TaskCriticality;
import com.weg.Maintenance_API.event.dto.response.EventResponseDto;
import com.weg.Maintenance_API.exception.type.ResourceNotFoundException;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    // Cria e persiste os dados da operacao.
    @Transactional
    public CalendarResponseDto create(CalendarCreateRequestDto request) {
        Event event = new Event();
        applyCreateFields(event, request);
        return eventMapper.toResponse(eventRepository.save(event));
    }

    @Transactional(readOnly = true)
    public List<EventResponseDto> getAllEvents() {
        return eventRepository.findAllForCalendar().stream()
                .map(event -> new EventResponseDto(
                        event.getScheduledFor().toLocalDate(),
                        event.getScheduledFor().toLocalTime(),
                        event.getScheduledAction()
                ))
                .toList();
    }

    // Busca os dados necessarios para esta operacao.
    @Transactional(readOnly = true)
    public Page<CalendarResponseDto> getAll(Pageable pageable) {
        return eventRepository.findAll(pageable).map(eventMapper::toResponse);
    }

    // Busca os dados necessarios para esta operacao.
    @Transactional(readOnly = true)
    public CalendarResponseDto getById(UUID id) {
        return eventMapper.toResponse(findById(id));
    }

    // Atualiza o estado conforme os dados informados.
    @Transactional
    public CalendarResponseDto patch(UUID id, CalendarUpdateRequestDto request) {
        Event event = findById(id);
        if (request.scheduledAction() != null) event.setScheduledAction(request.scheduledAction());
        if (request.criticality() != null) event.setCriticality(toCriticality(request.criticality()));
        if (request.scheduledFor() != null) event.setScheduledFor(request.scheduledFor());
        if (request.requestedAt() != null) event.setRequestedAt(request.requestedAt());
        if (request.studentId() != null) event.setStudent(findStudent(request.studentId()));
        if (request.teacherId() != null) event.setTeacher(findTeacher(request.teacherId()));
        if (request.equipmentId() != null) event.setEquipment(findEquipment(request.equipmentId()));
        if (request.machineId() != null) event.setMachine(findMachine(request.machineId()));
        if (request.placeId() != null) event.setPlace(findPlace(request.placeId()));
        if (request.maintenanceType() != null) event.setMaintenanceType(toMaintenanceType(request.maintenanceType()));
        if (request.status() != null) event.setStatus(toTaskSituation(request.status()));
        return eventMapper.toResponse(eventRepository.save(event));
    }

    // Remove ou invalida os dados solicitados.
    @Transactional
    public void delete(UUID id) {
        eventRepository.delete(findById(id));
    }

    // Aplica os dados recebidos na entidade.
    private void applyCreateFields(Event event, CalendarCreateRequestDto request) {
        event.setScheduledAction(request.scheduledAction());
        event.setCriticality(toCriticality(request.criticality()));
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

    // Busca os dados necessarios para esta operacao.
    private Event findById(UUID id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento", id));
    }

    // Busca os dados necessarios para esta operacao.
    private Student findStudent(UUID id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno", id));
    }

    // Busca os dados necessarios para esta operacao.
    private Teacher findTeacher(UUID id) {
        return teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Professor", id));
    }

    // Busca os dados necessarios para esta operacao.
    private Equipment findEquipment(UUID id) {
        return equipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipamento", id));
    }

    // Busca os dados necessarios para esta operacao.
    private Machine findMachine(UUID id) {
        return machineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maquina", id));
    }

    // Busca os dados necessarios para esta operacao.
    private Place findPlace(UUID id) {
        return placeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Local", id));
    }

    // Converte a criticidade validada no DTO para o enum persistido.
    private TaskCriticality toCriticality(String value) {
        return TaskCriticality.valueOf(value.trim().toUpperCase(Locale.ROOT));
    }

    // Converte o tipo de manutencao validado no DTO para o enum persistido.
    private com.weg.Maintenance_API.enums.MaintenanceType toMaintenanceType(String value) {
        return com.weg.Maintenance_API.enums.MaintenanceType.valueOf(
                value.trim().toUpperCase(Locale.ROOT)
        );
    }

    // Converte a situacao validada no DTO para o enum persistido.
    private com.weg.Maintenance_API.enums.TaskSituation toTaskSituation(String value) {
        return com.weg.Maintenance_API.enums.TaskSituation.valueOf(
                value.trim().toUpperCase(Locale.ROOT)
        );
    }
}
