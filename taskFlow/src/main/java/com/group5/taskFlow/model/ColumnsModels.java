package com.group5.taskFlow.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "columns")
@Getter
@Setter
public class ColumnsModels implements Serializable {

    static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // 👇 Evita loop de referência (columns → board)
    @ManyToOne
    @JoinColumn(name = "board_id", nullable = false)
    @JsonBackReference(value = "board-columns")
    private BoardModels board;

    @ManyToOne
    @JoinColumn(name = "column_type_id", nullable = false)
    private ColumnTypeModels columnType;

    @Column(name = "display_order", nullable = false)
    private Integer order;

    @OneToMany(mappedBy = "column", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CardsModels> cards = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ColumnsModels that = (ColumnsModels) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ColumnsModels{" +
                "id=" + id +
                ", order=" + order +
                '}';
    }
}
