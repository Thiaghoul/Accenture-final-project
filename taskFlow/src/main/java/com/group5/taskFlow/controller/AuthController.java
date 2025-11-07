package com.group5.taskFlow.controller;

import com.group5.taskFlow.dto.AuthResponse;
import com.group5.taskFlow.dto.PasswordResetDto;
import com.group5.taskFlow.dto.PasswordResetRequest;
import com.group5.taskFlow.dto.UserLoginRequest;
import com.group5.taskFlow.dto.UserRegisterRequest;
import com.group5.taskFlow.dto.UserResponse;
import com.group5.taskFlow.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerUser(@Valid @RequestBody UserRegisterRequest request) {
        AuthResponse authResponse = userService.registerUser(request);
        return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(@Valid @RequestBody UserLoginRequest request) {
        AuthResponse authResponse = userService.authenticateUser(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logoutUser() {
        // In a JWT-based system, logout typically involves client-side token removal.
        // Server-side invalidation might involve a blacklist, but for an MVP, client-side is often sufficient.
        SecurityContextHolder.clearContext(); // Clear security context on server side (if applicable)
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String userEmail = authentication.getName(); // Get email from authenticated principal
        UserResponse userResponse = userService.getCurrentUser(userEmail);
        return ResponseEntity.ok(userResponse);
    }

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
