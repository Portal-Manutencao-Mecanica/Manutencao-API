package com.weg.Maintenance_API.notification.entity;


import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "notification",
        indexes = {
                @Index(
                        name = "idx_notification_email",
                        columnList = "notification_email"
                ),
                @Index(
                        name = "idx_notification_status_read",
                        columnList = "notification_status_read"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "notification_id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "number_card", nullable = false, unique = true, length = 255)
    private String numberCard = java.util.UUID.randomUUID().toString();

    @Column(
            name = "notification_email",
            nullable = false,
            length = 150
    )
    private String email;

    @Column(
            name = "notification_title",
            nullable = false,
            length = 150
    )
    private String title;

    @Column(
            name = "notification_about",
            length = 255
    )
    private String about;

    @Column(
            name = "notification_description",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String description;

    @Column(
            name = "notification_status_read",
            nullable = false
    )
    private boolean statusRead = false;

    public Notification(
            String email,
            String title,
            String about,
            String description
    ) {
        this.email = email;
        this.title = title;
        this.about = about;
        this.description = description;
        this.statusRead = false;
    }

    /**
     * Garante que toda notificação nova comece como não lida.
     */
    @PrePersist
    protected void onCreate() {
        statusRead = false;
    }
}