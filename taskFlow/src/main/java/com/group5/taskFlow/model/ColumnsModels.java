package com.group5.taskFlow.model;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "columns")
@Data
public class ColumnsModels implements Serializable {

    static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "board_id", nullable = false)
    private BoardModels board;

    @ManyToOne
    @JoinColumn(name = "column_type_id", nullable = false)
    private ColumnTypeModels columnType;

    @Column(name = "display_order", nullable = false)
    private Integer order;

    @OneToMany(mappedBy = "column", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CardsModels> cards = new HashSet<>();

}
