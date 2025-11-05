package com.group5.taskFlow.model;


import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
@Table(name = "column_types")
public class ColumnTypeModels implements Serializable {

    static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(name = "display_order", nullable = false)
    private Integer order;

    @OneToMany(mappedBy = "columnType")
    private Set<ColumnsModels> columns = new HashSet<>();

}
