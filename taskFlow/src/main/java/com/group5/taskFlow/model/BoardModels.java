package com.group5.taskFlow.model;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "boards")
@Data
public class BoardModels implements Serializable {

    static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserModels owner;

    private Instant createdAt;
    private Instant updatedAt;

    public BoardModels() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ColumnsModels> columns = new HashSet<>();

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BoardMembersModels> members = new HashSet<>();
}
