package com.group5.taskFlow.model;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "columns")
@Data
public class ColumnsModels implements Serializable {

    static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToMany
    @JoinColumn(name = "board_id", nullable = false)
    private BoardModels board;

    @ManyToMany
    @JoinColumn(name = "column_type_id", nullable = false)
    private ColumnTypeModels columnType;

    private Integer order;

    @OneToMany(mappedBy = "columns", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CardsModels> cards;

}
