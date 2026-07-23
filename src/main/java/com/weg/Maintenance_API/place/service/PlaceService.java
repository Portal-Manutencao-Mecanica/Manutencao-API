package com.weg.Maintenance_API.place.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.weg.Maintenance_API.place.dto.request.PlacePatchRequest;
import com.weg.Maintenance_API.place.dto.request.PlaceRequest;
import com.weg.Maintenance_API.place.dto.response.PlaceResponse;
import com.weg.Maintenance_API.place.entity.Place;
import com.weg.Maintenance_API.place.mapper.PlaceMapper;
import com.weg.Maintenance_API.place.repository.PlaceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlaceService {

    private final PlaceRepository placeRepository;
    private final PlaceMapper placeMapper;

    @Transactional
    public PlaceResponse save(PlaceRequest placeRequest) {
        Place place = placeMapper.toEntity(placeRequest);
        place = placeRepository.save(place);
        return placeMapper.toResponse(place);
    }

    @Transactional(readOnly = true)
    public List<PlaceResponse> getAll() {
        return placeRepository.findAll().stream().map(placeMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public PlaceResponse getById(Long id) {
        Place place = placeRepository.findById(id).orElseThrow(() -> new RuntimeException(""));
        return placeMapper.toResponse(place);
    }

    @Transactional
    public PlaceResponse update(Long id, PlaceRequest placeRequest) {
        Place place = placeRepository.findById(id).orElseThrow(() -> new RuntimeException(""));
        place.setName(placeRequest.name());
        place = placeRepository.save(place);
        return placeMapper.toResponse(place);
    }

    @Transactional
    public PlaceResponse patch(Long id, PlacePatchRequest request) {
        Place place = placeRepository.findById(id).orElseThrow(() -> new RuntimeException(""));

        if (request.name() != null) {
            place.setName(request.name());
        }

        return placeMapper.toResponse(placeRepository.save(place));
    }

    @Transactional
    public void delete(Long id) {
        placeRepository.deleteById(id);
    }
}