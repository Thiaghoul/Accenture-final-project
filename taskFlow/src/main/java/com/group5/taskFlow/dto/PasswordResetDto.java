package com.group5.taskFlow.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class PasswordResetDto {
    @NotEmpty
    private String token;

    @NotEmpty
    private String password;
}
