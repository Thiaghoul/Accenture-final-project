package com.group5.taskFlow.model.pk;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class BoardMemberId implements Serializable {
    private UUID boardId;
    private UUID userId;
}
