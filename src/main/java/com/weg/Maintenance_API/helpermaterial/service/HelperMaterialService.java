package com.weg.Maintenance_API.helpermaterial.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.weg.Maintenance_API.helpermaterial.dto.request.HelperMaterialPatchRequest;
import com.weg.Maintenance_API.helpermaterial.dto.request.HelperMaterialRequest;
import com.weg.Maintenance_API.helpermaterial.dto.response.HelperMaterialResponse;
import com.weg.Maintenance_API.helpermaterial.entity.HelperMaterial;
import com.weg.Maintenance_API.helpermaterial.mapper.HelperMaterialMapper;
import com.weg.Maintenance_API.helpermaterial.repository.HelperMaterialRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HelperMaterialService {

    private final HelperMaterialMapper helperMaterialMapper;
    private final HelperMaterialRepository helperMaterialRepository;

    @Transactional
    public HelperMaterialResponse save(HelperMaterialRequest helperMaterialRequest) {
        HelperMaterial helperMaterial = helperMaterialMapper.toEntity(helperMaterialRequest);
        helperMaterial = helperMaterialRepository.save(helperMaterial);
        return helperMaterialMapper.toResponse(helperMaterial);
    }

    @Transactional(readOnly = true)
    public List<HelperMaterialResponse> getAll() {
        return helperMaterialRepository.findAll().stream().map(helperMaterialMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public HelperMaterialResponse getById(Long id) {
        HelperMaterial helperMaterial = helperMaterialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(""));
        return helperMaterialMapper.toResponse(helperMaterial);
    }

    @Transactional
    public HelperMaterialResponse update(Long id, HelperMaterialRequest helperMaterialRequest) {
        HelperMaterial helperMaterial = helperMaterialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(""));
        helperMaterial.setTitle(helperMaterialRequest.title());
        helperMaterial.setDescription(helperMaterialRequest.description());
        helperMaterial.setUrl(helperMaterialRequest.url());
        helperMaterial.setType(helperMaterialRequest.type());
        return helperMaterialMapper.toResponse(helperMaterialRepository.save(helperMaterial));
    }

    @Transactional
    public HelperMaterialResponse patch(Long id, HelperMaterialPatchRequest request) {
        HelperMaterial helperMaterial = helperMaterialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(""));

        if (request.title() != null) {
            helperMaterial.setTitle(request.title());
        }
        if (request.description() != null) {
            helperMaterial.setDescription(request.description());
        }
        if (request.url() != null) {
            helperMaterial.setUrl(request.url());
        }
        if (request.type() != null) {
            helperMaterial.setType(request.type());
        }

        return helperMaterialMapper.toResponse(helperMaterialRepository.save(helperMaterial));
    }

    @Transactional
    public void delete(Long id) {
        helperMaterialRepository.deleteById(id);
    }
}