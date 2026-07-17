package com.weg.Manutencao_API.local.mapper;

import org.springframework.stereotype.Component;

import com.weg.Manutencao_API.local.dto.PlaceResponse;
import com.weg.Manutencao_API.local.entity.Place;

@Component
public interface PlaceInterface {


    public PlaceResponse toResponse(Place place, List<MachineResponse> machines){
        return new PlaceResponse(null, null, machines);
    }
}
