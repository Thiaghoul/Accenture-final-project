package com.group5.taskFlow.model;

import com.group5.taskFlow.model.enums.EventType;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "activity_logs")
@Data
public class ActivityLogsModels implements Serializable {

    static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "card_id", nullable = false)
    private CardsModels card;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserModels user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType eventType;

    @Column(columnDefinition = "TEXT")
    private String details;

    private Instant timestamp = Instant.now();

}
