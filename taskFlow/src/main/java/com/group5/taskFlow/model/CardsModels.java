package com.group5.taskFlow.model;

import com.group5.taskFlow.model.enums.Priority;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "cards")
@Data
public class CardsModels implements Serializable {

    static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority;

    private LocalDate dueDate;

    @Column(columnDefinition = "INT DEFAULT 0")
    private Integer completionPercentage = 0;

    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();

    @ManyToOne
    @JoinColumn(name = "column_id", nullable = false)
    private ColumnsModels column;

    @ManyToOne
    @JoinColumn(name = "assignee_id")
    private UserModels assignee;

    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CommentsModels> comments = new HashSet<>();

    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ActivityLogsModels> activityLogs = new HashSet<>();
}
