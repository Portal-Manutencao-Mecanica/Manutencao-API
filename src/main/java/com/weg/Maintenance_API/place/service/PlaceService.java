package com.weg.Maintenance_API.place.service;

import java.util.List;

import org.springframework.stereotype.Service;

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

    public PlaceResponse save(PlaceRequest placeRequest) {
        Place place = placeMapper.toEntity(placeRequest);

        place = placeRepository.save(place);

        return placeMapper.toResponse(place);
    }

    public List<PlaceResponse> getAll() {
        List<Place> places = placeRepository.findAll();

        return places.stream().map(placeMapper::toResponse).toList();
    }

    public PlaceResponse getById(Long id) {
        Place place = placeRepository.findById(id).orElseThrow(() -> new RuntimeException("")); // Falta a mensagem

        return placeMapper.toResponse(place);
    }

    public PlaceResponse update(Long id, PlaceRequest placeRequest) {
        Place place = placeRepository.findById(id).orElseThrow(() -> new RuntimeException("")); // Falta a mensagem

        place.setName(placeRequest.name());

        place = placeRepository.save(place);

        return placeMapper.toResponse(place);
    }

    public void delete(Long id) {
        placeRepository.deleteById(id);
    }
}
