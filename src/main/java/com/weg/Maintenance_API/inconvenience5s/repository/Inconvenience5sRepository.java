package com.weg.Maintenance_API.inconvenience5s.repository;

import com.weg.Maintenance_API.inconvenience5s.entity.Inconvenience5S;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Inconvenience5sRepository extends JpaRepository<Inconvenience5S, Long> {
    
}


