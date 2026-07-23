package com.weg.Maintenance_API.media.entity;


import java.util.UUID;

import com.weg.Maintenance_API.enums.MediaType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "media")
@Getter
@Setter
@NoArgsConstructor
public class Media {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "media_id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "number_card", nullable = false, unique = true, length = 255)
    private String numberCard = java.util.UUID.randomUUID().toString();

    @Column(name = "media_description", length = 255)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "media_type", nullable = false, length = 50)
    private MediaType mediaType;

    @Column(name = "storage_key", unique = true, length = 500,columnDefinition = "TEXT")
    private String image;

    @Column(name = "original_name", nullable = false, length = 255)
    private String originalName;

    @Column(name = "content_type", nullable = false, length = 100)
    private String contentType;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
