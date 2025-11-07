package com.group5.taskFlow.controller;

import com.group5.taskFlow.dto.PasswordResetDto;
import com.group5.taskFlow.dto.PasswordResetRequest;
import com.group5.taskFlow.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody PasswordResetRequest passwordResetRequest) {
        userService.createPasswordResetTokenForUser(passwordResetRequest.getEmail());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody PasswordResetDto passwordResetDto) {
        userService.resetPassword(passwordResetDto.getToken(), passwordResetDto.getPassword());
        return ResponseEntity.ok().build();
    }
}
