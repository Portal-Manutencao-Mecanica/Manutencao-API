package com.weg.Maintenance_API.notification.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "notification")
@Getter
@Setter
@NoArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @Column(name = "notification_email")
    private String email;

    @Column(name = "notification_title")
    private String title;

    @Column(name = "notification_about")
    private String about;

    @Column(name = "notification_description")
    private String description;

    @Column(name = "notification_status_read")
    private Boolean statusRead;

    public Notification(String email, String title, String about, String description) {
        this.email = email;
        this.title = title;
        this.about = about;
        this.description = description;
        this.statusRead = false;
    }

}
