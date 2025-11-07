package com.group5.taskFlow.model;

import java.io.Serializable;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class BoardMemberId implements Serializable {

    @Column(name = "board_id")
    private UUID board;

    @Column(name = "user_id")
    private UUID user;
}
