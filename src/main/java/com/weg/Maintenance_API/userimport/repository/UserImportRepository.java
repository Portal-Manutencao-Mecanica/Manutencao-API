package com.weg.Maintenance_API.userimport.repository;

import com.weg.Maintenance_API.userimport.entity.UserImport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserImportRepository extends JpaRepository<UserImport, UUID> {
}
