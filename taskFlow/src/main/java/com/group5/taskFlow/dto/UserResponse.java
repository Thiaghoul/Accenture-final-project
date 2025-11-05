package com.group5.taskFlow.dto;

import com.group5.taskFlow.model.enums.UserRoles;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class UserResponse {
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private Set<UserRoles> roles;
}
