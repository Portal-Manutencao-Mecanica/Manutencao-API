package com.weg.Maintenance_API.machine.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.weg.Maintenance_API.machine.entity.Machine;
import com.weg.Maintenance_API.enums.EquipmentCondition;

import java.util.UUID;

@Repository
public interface MachineRepository extends JpaRepository<Machine, UUID>{
    
    @Query("""
            select machine
              from Machine machine
              join machine.place place
             where (:search is null
                    or lower(machine.name) like lower(concat('%', :search, '%'))
                    or lower(machine.patrimony) like lower(concat('%', :search, '%'))
                    or lower(coalesce(machine.tag, '')) like lower(concat('%', :search, '%'))
                    or lower(place.name) like lower(concat('%', :search, '%')))
               and (:condition is null or machine.condition = :condition)
            """)
    Page<Machine> findAllFiltered(
            @Param("search") String search,
            @Param("condition") EquipmentCondition condition,
            Pageable pageable
    );
}
