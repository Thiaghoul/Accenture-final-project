package com.group5.taskFlow.dto;

import com.group5.taskFlow.model.enums.MemberRoles;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class AddMemberRequest {
    @NotNull
    private UUID userId;

    @NotNull
    private MemberRoles role;
}