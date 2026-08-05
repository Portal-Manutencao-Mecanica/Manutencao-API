package com.weg.Maintenance_API.equipment.repository;


import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.weg.Maintenance_API.equipment.entity.Equipment;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, UUID> {

    @Query("""
            select equipment
              from Equipment equipment
             where (:search is null
                    or lower(equipment.name) like lower(concat('%', :search, '%'))
                    or lower(coalesce(equipment.sap, '')) like lower(concat('%', :search, '%'))
                    or lower(coalesce(equipment.patrimony, '')) like lower(concat('%', :search, '%'))
                    or lower(coalesce(equipment.tag, '')) like lower(concat('%', :search, '%')))
            """)
    Page<Equipment> findAllFiltered(@Param("search") String search, Pageable pageable);
}
