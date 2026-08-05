package com.weg.Maintenance_API.buy.service;


import java.util.UUID;

import com.weg.Maintenance_API.exception.type.ResourceNotFoundException;
import com.weg.Maintenance_API.service.EntityReferenceService;

import com.weg.Maintenance_API.buy.dto.request.BuyDtoRequest;
import com.weg.Maintenance_API.buy.dto.request.BuyPatchRequest;
import com.weg.Maintenance_API.buy.dto.response.BuyDtoResponse;
import com.weg.Maintenance_API.buy.entity.Buy;
import com.weg.Maintenance_API.buy.entity.BuyItem;
import com.weg.Maintenance_API.equipment.entity.Equipment;
import com.weg.Maintenance_API.enums.BuyStatus;
import com.weg.Maintenance_API.buy.mapper.BuyMapper;
import com.weg.Maintenance_API.buy.mapper.BuyItemMapper;
import com.weg.Maintenance_API.buy.repository.BuyRepository;
import com.weg.Maintenance_API.user.service.AuthenticatedUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class BuyService {
    private final BuyRepository repository;
    private final BuyMapper mapper;
    private final BuyItemMapper itemMapper;
    private final EntityReferenceService references;
    private final AuthenticatedUserService authenticatedUserService;

    // Cria e persiste os dados da operacao.
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public BuyDtoResponse create(BuyDtoRequest request){
        Buy entity = mapper.toEntity(request);
        entity.setCreatedBy(authenticatedUserService.requireCurrentUser());
        entity.setClassGroup(references.classGroup(request.classGroupId()));
        entity.setNotifiedTeacher(request.notifiedTeacherId() == null ? null : references.teacher(request.notifiedTeacherId()));
        applyItems(entity, request);
        repository.save(entity);
        return mapper.toResponse(entity);
    }

    // Busca os dados necessarios para esta operacao.
    @Transactional(isolation = Isolation.READ_COMMITTED,  readOnly = true)
    public org.springframework.data.domain.Page<BuyDtoResponse> getAll(
            String search,
            BuyStatus status,
            org.springframework.data.domain.Pageable pageable
    ) {
        String normalizedSearch = search == null || search.isBlank()
                ? null
                : search.trim();
        return repository.findAllFiltered(
                normalizedSearch,
                status,
                pageable
        ).map(mapper::toResponse);
    }

    // Busca os dados necessarios para esta operacao.
    @Transactional(isolation = Isolation.READ_COMMITTED,  readOnly = true)
    public BuyDtoResponse getById(UUID id){
        Buy entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Compra", id));
        return mapper.toResponse(entity);
    }

    // Atualiza o estado conforme os dados informados.
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public BuyDtoResponse update(UUID id, BuyDtoRequest request){
        Buy entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Compra", id));

        entity.setPurchaseJustification(request.purchaseJustification());
        entity.setClassGroup(references.classGroup(request.classGroupId()));
        entity.setNotifiedTeacher(request.notifiedTeacherId() == null
                ? null
                : references.teacher(request.notifiedTeacherId()));
        applyItems(entity, request);

        repository.save(entity);
        return mapper.toResponse(entity);
    }
    // READ_COMMITTED sets the transaction isolation level.
    // It reads only data committed by other transactions.
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public BuyDtoResponse patch(UUID id, BuyPatchRequest request){
        Buy entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Compra", id));

        if (request.purchaseJustification() != null) {
            entity.setPurchaseJustification(request.purchaseJustification());
        }

        repository.save(entity);
        return mapper.toResponse(entity);
    }

    // Remove ou invalida os dados solicitados.
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deleteById(UUID id){
        repository.delete(repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Compra", id)));
    }

    // Busca os dados necessarios para esta operacao.
    @Transactional(isolation = Isolation.READ_COMMITTED,  readOnly = true)
    public org.springframework.data.domain.Page<BuyDtoResponse> getByStatus(
            String status,
            org.springframework.data.domain.Pageable pageable
    ) {
        BuyStatus buyStatus = BuyStatus.valueOf(status.trim().toUpperCase(Locale.ROOT));
        return repository.findAllByStatus(buyStatus, pageable).map(mapper::toResponse);
    }

    private void applyItems(Buy entity, BuyDtoRequest request) {
        entity.getItems().clear();
        request.items().forEach(requestItem -> {
            BuyItem item = itemMapper.toEntity(requestItem);
            Equipment equipment = references.equipment(requestItem.equipmentId());
            item.setEquipment(equipment);
            item.setSap(equipment.getSap());
            item.setPatrimony(equipment.getPatrimony());
            item.setTag(equipment.getTag());
            item.setBuy(entity);
            entity.getItems().add(item);
        });
    }
}
