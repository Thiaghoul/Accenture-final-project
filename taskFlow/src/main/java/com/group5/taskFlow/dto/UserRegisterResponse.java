package com.group5.taskFlow.dto;

import jakarta.validation.constraints.NotBlank;

public record UserRegisterResponse(@NotBlank String email, @NotBlank String token) {
}