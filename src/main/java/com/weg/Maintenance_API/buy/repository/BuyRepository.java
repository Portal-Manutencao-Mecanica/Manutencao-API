package com.weg.Maintenance_API.buy.repository;


import java.util.UUID;

import com.weg.Maintenance_API.buy.entity.Buy;
import com.weg.Maintenance_API.enums.BuyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface BuyRepository extends JpaRepository<Buy, UUID> {

    Page<Buy> findAllByStatus(BuyStatus status, Pageable pageable);

    @Query("""
            select buy
              from Buy buy
              join buy.createdBy creator
              join buy.classGroup classGroup
              left join buy.notifiedTeacher teacher
             where (:search is null
                    or lower(buy.purchaseJustification) like lower(concat('%', :search, '%'))
                    or lower(creator.name) like lower(concat('%', :search, '%'))
                    or lower(classGroup.acronym) like lower(concat('%', :search, '%'))
                    or lower(coalesce(teacher.name, '')) like lower(concat('%', :search, '%')))
               and (:status is null or buy.status = :status)
            """)
    Page<Buy> findAllFiltered(
            @Param("search") String search,
            @Param("status") BuyStatus status,
            Pageable pageable
    );
}
