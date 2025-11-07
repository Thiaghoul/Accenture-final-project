package com.group5.taskFlow.model;

import com.group5.taskFlow.model.enums.MemberRoles;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Entity
@Table(name = "board_members")
@Data
public class BoardMembersModels implements Serializable {

    @EmbeddedId
    private BoardMemberId id;

    @ManyToOne
    @MapsId("user")
    @JoinColumn(name = "user_id")
    private UserModels user;

    @ManyToOne
    @MapsId("board")
    @JoinColumn(name = "board_id")
    private BoardModels board;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private MemberRoles role;
}