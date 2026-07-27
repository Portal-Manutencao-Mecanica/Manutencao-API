package com.weg.Maintenance_API.media.repository;

import com.weg.Maintenance_API.media.entity.Media;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MediaRepository extends JpaRepository<Media, UUID> {
}
