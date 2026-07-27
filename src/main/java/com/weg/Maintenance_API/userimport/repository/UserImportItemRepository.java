package com.weg.Maintenance_API.userimport.repository;

import com.weg.Maintenance_API.userimport.entity.UserImportItem;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserImportItemRepository extends JpaRepository<UserImportItem, UUID> {

    @EntityGraph(attributePaths = "createdUser")
    List<UserImportItem> findAllByUserImportIdOrderByRowNumber(UUID userImportId);
}
