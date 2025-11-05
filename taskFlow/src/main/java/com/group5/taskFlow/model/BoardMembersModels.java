package com.group5.taskFlow.model;

import com.group5.taskFlow.model.enums.MemberRoles;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "board_members")
@Data
@IdClass(BoardMemberId.class)
public class BoardMembersModels implements Serializable {

    static final long serialVersionUID = 1L;

    @Id
    @ManyToOne
    @JoinColumn(name = "board_id", nullable = false)
    private BoardModels board;

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserModels user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberRoles role;

    private Instant joinedAt = Instant.now();
}
