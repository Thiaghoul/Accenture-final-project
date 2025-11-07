package com.group5.taskFlow.dto;

import com.group5.taskFlow.model.enums.MemberRoles;
import lombok.Data;

import java.util.UUID;

@Data
public class BoardMemberResponse {
    private UUID userId;
    private UUID boardId;
    private MemberRoles role;
}
