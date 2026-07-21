package com.weg.Maintenance_API.buy.service;

import com.weg.Maintenance_API.buy.dto.request.BuyDtoRequest;
import com.weg.Maintenance_API.buy.dto.response.BuyDtoResponse;
import com.weg.Maintenance_API.buy.entity.Buy;
import com.weg.Maintenance_API.buy.mapper.BuyMapper;
import com.weg.Maintenance_API.buy.repository.BuyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = RuntimeException.class,readOnly = true)
    public List<BuyDtoResponse> getAll(){
        return repository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = RuntimeException.class,readOnly = true)
    public BuyDtoResponse getById(Long id){
        Buy entity = repository.findById(id).orElseThrow(() -> new RuntimeException(""));
        return mapper.toResponse(entity);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = RuntimeException.class)
    public BuyDtoResponse update(Long id,BuyDtoRequest request){
        Buy entity = repository.findById(id).orElseThrow(() -> new RuntimeException(""));
        Buy entity2 = mapper.toEntity(request);
        entity2.setId(entity.getId());
        repository.save(entity2);
        return mapper.toResponse(entity2);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = RuntimeException.class)
    public void deleteById(Long id){
        repository.deleteById(id);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = RuntimeException.class,readOnly = true)
    public List<BuyDtoResponse> getByStatus(String status){
        return repository.findAllByStatus(status).orElseThrow(() -> new RuntimeException(""));
    }
}
