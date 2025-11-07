package com.group5.taskFlow.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class PasswordResetRequest {
    @Email
    @NotEmpty
    private String email;
}
