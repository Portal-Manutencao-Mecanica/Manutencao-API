package com.weg.Maintenance_API.inconvenience5s.service;


import java.util.UUID;

import com.weg.Maintenance_API.enums.RegistrationPeriod;
import com.weg.Maintenance_API.enums.Inconvenience5SStatus;
import com.weg.Maintenance_API.enums.MediaType;
import com.weg.Maintenance_API.enums.Role;

import com.weg.Maintenance_API.exception.type.InvalidRequestException;
import com.weg.Maintenance_API.exception.type.ResourceNotFoundException;
import com.weg.Maintenance_API.media.service.ImageMediaFactory;
import com.weg.Maintenance_API.student.entity.Student;
import com.weg.Maintenance_API.user.entity.User;
import com.weg.Maintenance_API.user.service.AuthenticatedUserService;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.data.jpa.domain.Specification;

import com.weg.Maintenance_API.inconvenience5s.dto.requests.Inconvenience5SDtoRequest;
import com.weg.Maintenance_API.inconvenience5s.dto.requests.Inconvenience5SPatchRequest;
import com.weg.Maintenance_API.inconvenience5s.dto.response.Inconvenience5SDtoResponse;
import com.weg.Maintenance_API.inconvenience5s.entity.Inconvenience5S;
import com.weg.Maintenance_API.inconvenience5s.mapper.Inconvenience5SMapper;
import com.weg.Maintenance_API.inconvenience5s.repository.Inconvenience5sRepository;
import com.weg.Maintenance_API.service.EntityReferenceService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class Inconvenience5sService {

    private final Inconvenience5SMapper inconvenience5sMapper;
    private final Inconvenience5sRepository inconvenience5sRepository;
    private final EntityReferenceService references;
    private final AuthenticatedUserService authenticatedUserService;
    private final ImageMediaFactory imageMediaFactory;

    // Cria e persiste os dados da operacao.
    @Transactional
    public Inconvenience5SDtoResponse save(Inconvenience5SDtoRequest request) {
        User currentUser = authenticatedUserService.requireCurrentUser();
        Inconvenience5S inconvenience5s = inconvenience5sMapper.toEntity(request);
        applyReferences(inconvenience5s, request);
        inconvenience5s.setCreatedBy(currentUser);
        inconvenience5s.setStatus(Inconvenience5SStatus.EM_ANALISE);
        inconvenience5s.setMedia(imageMediaFactory.fromDataUrls(
                request.images(),
                currentUser,
                MediaType.INCONVENIENCE_5S,
                "inconveniencia-5s",
                "Evidência da inconveniência 5S",
                false
        ));
        inconvenience5s = inconvenience5sRepository.save(inconvenience5s);
        return inconvenience5sMapper.toResponse(inconvenience5s);
    }

    // Busca os dados necessarios para esta operacao.
    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<Inconvenience5SDtoResponse> getAll(
            org.springframework.data.domain.Pageable pageable
    ) {
        User currentUser = authenticatedUserService.requireCurrentUser();
        Specification<Inconvenience5S> visibility = (root, query, builder) -> {
            if (!(currentUser instanceof Student student)) {
                return builder.conjunction();
            }
            List<UUID> classGroupIds = student.getClassGroups().stream()
                    .map(classGroup -> classGroup.getId())
                    .toList();
            if (classGroupIds.isEmpty()) {
                return builder.disjunction();
            }
            return root.get("classGroup").get("id").in(classGroupIds);
        };
        return inconvenience5sRepository.findAll(visibility, pageable)
                .map(inconvenience5sMapper::toResponse);
    }

    // Busca os dados necessarios para esta operacao.
    @Transactional(readOnly = true)
    public Inconvenience5SDtoResponse getById(UUID id) {
        Inconvenience5S inconvenience5s = inconvenience5sRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inconvenience5S", id));
        ensureCanView(inconvenience5s, authenticatedUserService.requireCurrentUser());
        return inconvenience5sMapper.toResponse(inconvenience5s);
    }

    // Atualiza o estado conforme os dados informados.
    @Transactional
    public Inconvenience5SDtoResponse update(UUID id, Inconvenience5SDtoRequest request) {
        User currentUser = requireRole(Role.ADMIN);
        Inconvenience5S inconvenience5s = inconvenience5sRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inconvenience5S", id));
        applyReferences(inconvenience5s, request);
        inconvenience5s.setInconvenience(request.inconvenience());
        inconvenience5s.setDescription(request.description());
        inconvenience5s.setRegistrationPeriod(request.registrationPeriod());
        inconvenience5s.getMedia().clear();
        inconvenience5s.getMedia().addAll(imageMediaFactory.fromDataUrls(
                request.images(),
                currentUser,
                MediaType.INCONVENIENCE_5S,
                "inconveniencia-5s",
                "Evidência da inconveniência 5S",
                false
        ));
        return inconvenience5sMapper.toResponse(inconvenience5sRepository.save(inconvenience5s));
    }

    // Atualiza o estado conforme os dados informados.
    @Transactional
    public Inconvenience5SDtoResponse patch(UUID id, Inconvenience5SPatchRequest request) {
        User currentUser = authenticatedUserService.requireCurrentUser();
        if (currentUser.getRole() != Role.ADMIN && currentUser.getRole() != Role.COORDENADOR) {
            throw new AccessDeniedException("Você não possui permissão para alterar esta inconveniência.");
        }
        if (currentUser.getRole() == Role.COORDENADOR && hasContentChanges(request)) {
            throw new AccessDeniedException("Coordenadores podem alterar somente a situação da inconveniência.");
        }
        Inconvenience5S inconvenience5s = inconvenience5sRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inconvenience5S", id));

        if (request.inconvenience() != null) {
        inconvenience5s.setInconvenience(request.inconvenience());
        }
        if (request.description() != null) {
            inconvenience5s.setDescription(request.description());
        }
        if (request.registrationPeriod() != null) {
            inconvenience5s.setRegistrationPeriod(RegistrationPeriod.valueOf(
                    request.registrationPeriod().trim().toUpperCase(java.util.Locale.ROOT)
            ));
        }
        if (request.status() != null) {
            inconvenience5s.setStatus(allowedStatus(request.status()));
        } else if (currentUser.getRole() == Role.COORDENADOR) {
            throw new InvalidRequestException("Informe a situação da inconveniência.");
        }

        return inconvenience5sMapper.toResponse(inconvenience5sRepository.save(inconvenience5s));
    }

    // Remove ou invalida os dados solicitados.
    @Transactional
    public void delete(UUID id) {
        requireRole(Role.ADMIN);
        inconvenience5sRepository.delete(inconvenience5sRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Inconvenience5S", id)));
    }

    private boolean hasContentChanges(Inconvenience5SPatchRequest request) {
        return request.inconvenience() != null
                || request.description() != null
                || request.registrationPeriod() != null;
    }

    private Inconvenience5SStatus allowedStatus(String value) {
        Inconvenience5SStatus status = Inconvenience5SStatus.valueOf(
                value.trim().toUpperCase(java.util.Locale.ROOT)
        );
        if (status != Inconvenience5SStatus.EM_ANALISE
                && status != Inconvenience5SStatus.APROVADA
                && status != Inconvenience5SStatus.REPROVADA) {
            throw new InvalidRequestException(
                    "Situações permitidas: EM_ANALISE, APROVADA ou REPROVADA."
            );
        }
        return status;
    }

    private void ensureCanView(Inconvenience5S inconvenience, User currentUser) {
        if (!(currentUser instanceof Student student)) {
            return;
        }
        boolean sameClassGroup = student.getClassGroups().stream()
                .anyMatch(classGroup -> classGroup.getId().equals(inconvenience.getClassGroup().getId()));
        if (!sameClassGroup) {
            throw new AccessDeniedException("Esta inconveniência não pertence a uma turma do aluno.");
        }
    }

    private User requireRole(Role role) {
        User currentUser = authenticatedUserService.requireCurrentUser();
        if (currentUser.getRole() != role) {
            throw new AccessDeniedException("Você não possui permissão para executar esta ação.");
        }
        return currentUser;
    }
    // Aplica os dados recebidos na entidade.
    private void applyReferences(Inconvenience5S entity, Inconvenience5SDtoRequest request) {
        entity.setPlace(references.place(request.placeId()));
        entity.setNotifiedTeacher(references.teacher(request.notifiedTeacherId()));
        entity.setClassGroup(references.classGroup(request.classGroupId()));
        entity.setInvolvedStudents(references.students(request.involvedStudentIds()));
    }}
