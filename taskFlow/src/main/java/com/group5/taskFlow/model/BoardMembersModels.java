package com.group5.taskFlow.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.group5.taskFlow.model.enums.MemberRoles;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "board_members")
@Getter
@Setter
@IdClass(BoardMemberId.class)
public class BoardMembersModels implements Serializable {

    static final long serialVersionUID = 1L;

    @Id
    @ManyToOne
    @JoinColumn(name = "board_id", nullable = false)
    @JsonBackReference(value = "board-members")
    private BoardModels board;

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserModels user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MemberRoles role;

    private Instant joinedAt = Instant.now();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BoardMembersModels that = (BoardMembersModels) o;
        return Objects.equals(board, that.board) && Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, user);
    }

    @Override
    public String toString() {
        return "BoardMembersModels{" +
                "boardId=" + (board != null ? board.getId() : "null") +
                ", userId=" + (user != null ? user.getId() : "null") +
                ", role=" + role +
                '}';
    }
}
