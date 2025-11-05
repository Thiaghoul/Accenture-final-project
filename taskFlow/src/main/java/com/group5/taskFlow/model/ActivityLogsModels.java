package com.group5.taskFlow.model;

import jakarta.persistence.*;
import lombok.Data;

import javax.smartcardio.Card;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "activity_logs")
@Data
public class ActivityLogsModels implements Serializable {

    static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserModels user;

    @Enumerated(EnumType.STRING)
    private ActivityLogsModels eventType;

    @Column(columnDefinition = "TEXT")
    private String details;

    private Instant timestamp = Instant.now();


}
