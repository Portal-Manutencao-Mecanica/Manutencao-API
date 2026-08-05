package com.weg.Maintenance_API.student.repository;


import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

import com.weg.Maintenance_API.student.entity.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, UUID> {
    
    Page<Student> findAllByEnabledTrue(Pageable pageable);

    @Query("""
            select student
              from Student student
             where (:search is null
                    or lower(student.name) like lower(concat('%', :search, '%'))
                    or lower(student.email) like lower(concat('%', :search, '%'))
                    or lower(student.numberCard) like lower(concat('%', :search, '%')))
               and (:enabled is null or student.enabled = :enabled)
            """)
    Page<Student> findAllFiltered(
            @Param("search") String search,
            @Param("enabled") Boolean enabled,
            Pageable pageable
    );
}
