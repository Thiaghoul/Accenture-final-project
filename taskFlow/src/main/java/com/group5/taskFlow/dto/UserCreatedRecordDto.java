package com.group5.taskFlow.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import javax.management.relation.Role;

public record UserCreatedRecordDto (@NotBlank @Email String email,
                                    @NotBlank String password,
                                    @NotBlank String firstName,
                                    @NotBlank String lastName,
                                    @NotBlank Role role
                                    )
{
}
