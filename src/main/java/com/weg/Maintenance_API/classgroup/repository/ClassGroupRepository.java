package com.weg.Maintenance_API.classgroup.repository;


import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.weg.Maintenance_API.classgroup.entity.ClassGroup;

@Repository
public interface ClassGroupRepository extends JpaRepository<ClassGroup, UUID>{
    
    Page<ClassGroup> findAllByEnabledTrue(Pageable pageable);

    @Query("""
            select classGroup
              from ClassGroup classGroup
             where (:search = ''
                    or lower(classGroup.acronym) like lower(concat('%', :search, '%')))
               and (:enabled is null or classGroup.enabled = :enabled)
            """)
    Page<ClassGroup> findAllFiltered(
            @Param("search") String search,
            @Param("enabled") Boolean enabled,
            Pageable pageable
    );
}
