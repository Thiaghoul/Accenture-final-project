package com.group5.taskFlow.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "comments")
@Getter
@Setter
public class CommentsModels implements Serializable {

    static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "card_id", nullable = false)
    private CardsModels card;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserModels user;

    @Column(nullable = false)
    private String text;

    private Instant createdAt = Instant.now();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommentsModels that = (CommentsModels) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "CommentsModels{" +
                "id=" + id +
                ", text='" + text + '\'' +
                '}';
    }
}
