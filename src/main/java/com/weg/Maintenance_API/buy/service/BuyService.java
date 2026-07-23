package com.weg.Maintenance_API.buy.service;


import java.util.UUID;

import com.weg.Maintenance_API.exception.type.ResourceNotFoundException;

import com.weg.Maintenance_API.buy.dto.request.BuyDtoRequest;
import com.weg.Maintenance_API.buy.dto.request.BuyPatchRequest;
import com.weg.Maintenance_API.buy.dto.response.BuyDtoResponse;
import com.weg.Maintenance_API.buy.entity.Buy;
import com.weg.Maintenance_API.enums.BuyStatus;
import com.weg.Maintenance_API.buy.mapper.BuyMapper;
import com.weg.Maintenance_API.buy.repository.BuyRepository;
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

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = RuntimeException.class)
    public BuyDtoResponse create(BuyDtoRequest request){
        Buy entity = mapper.toEntity(request);
        repository.save(entity);
        return mapper.toResponse(entity);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = RuntimeException.class, readOnly = true)
    public List<BuyDtoResponse> getAll(){
        return repository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = RuntimeException.class, readOnly = true)
    public BuyDtoResponse getById(UUID id){
        Buy entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Compra", id));
        return mapper.toResponse(entity);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = RuntimeException.class)
    public BuyDtoResponse update(UUID id, BuyDtoRequest request){
        Buy entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Compra", id));

        entity.setPurchaseJustification(request.purchaseJustification());

        repository.save(entity);
        return mapper.toResponse(entity);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = RuntimeException.class)
    public BuyDtoResponse patch(UUID id, BuyPatchRequest request){
        Buy entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Compra", id));

        if (request.purchaseJustification() != null) {
            entity.setPurchaseJustification(request.purchaseJustification());
        }

        repository.save(entity);
        return mapper.toResponse(entity);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = RuntimeException.class)
    public void deleteById(UUID id){
        repository.deleteById(id);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = RuntimeException.class, readOnly = true)
    public List<BuyDtoResponse> getByStatus(String status){
        BuyStatus buyStatus = BuyStatus.valueOf(status.trim().toUpperCase(Locale.ROOT));
        return repository.findAllByStatus(buyStatus)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }
}