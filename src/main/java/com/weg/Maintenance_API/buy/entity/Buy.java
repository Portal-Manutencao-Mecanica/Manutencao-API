package com.weg.Maintenance_API.buy.entity;

import com.weg.Maintenance_API.classgroup.entity.ClassGroup;
import com.weg.Maintenance_API.enums.BuyStatus;
import com.weg.Maintenance_API.media.entity.Media;
import com.weg.Maintenance_API.teacher.entity.Teacher;
import com.weg.Maintenance_API.user.entity.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "buy")
@Getter
@Setter
@NoArgsConstructor
public class Buy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "buy_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private BuyStatus status = BuyStatus.NAO_VISUALIZADO;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notified_teacher_id")
    private Teacher notifiedTeacher;

    @Column(name = "purchase_justification", columnDefinition = "TEXT")
    private String purchaseJustification;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_group_id")
    private ClassGroup classGroup;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(
            mappedBy = "buy",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<BuyItem> items = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(
            name = "buy_media",
            joinColumns = @JoinColumn(name = "buy_id"),
            inverseJoinColumns = @JoinColumn(name = "media_id")
    )
    private List<Media> media = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = BuyStatus.NAO_VISUALIZADO;
        }
    }
}
