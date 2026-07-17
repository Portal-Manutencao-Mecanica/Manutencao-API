package com.weg.Manutencao_API.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.weg.Manutencao_API.admin.entity.Admin;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    
}
