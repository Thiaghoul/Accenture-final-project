package com.group5.taskFlow.controller;

import com.group5.taskFlow.dto.*;
import com.group5.taskFlow.service.LoginHistoryService;
import com.group5.taskFlow.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserService userService;
    private final LoginHistoryService loginHistoryService;

    public UserController(UserService userService, LoginHistoryService loginHistoryService) {
        this.userService = userService;
        this.loginHistoryService = loginHistoryService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody UserRequest userRequest) {
        log.info("Received request to register user with email: {}", userRequest.getEmail());
        UserResponse newUser = userService.save(userRequest);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<UserRegisterResponse> loginUser(@Valid @RequestBody UserLoginRequest userLoginRequest) {
        log.info("Received request to login user with email: {}", userLoginRequest.getEmail());
        UserRegisterResponse response = userService.authenticateUser(userLoginRequest.getEmail(), userLoginRequest.getPassword());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        log.info("Received request to get all users");
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
        log.info("Received request to get user with id: {}", id);
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{id}/login-history")
    public ResponseEntity<Page<LoginHistoryResponse>> getLoginHistory(@PathVariable UUID id, Pageable pageable) {
        log.info("Received request to get login history for user with id: {}", id);
        Page<LoginHistoryResponse> history = loginHistoryService.getLoginHistory(id, pageable);
        return ResponseEntity.ok(history);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable UUID id, @Valid @RequestBody UserUpdateRequest userUpdateRequest) {
        log.info("Received request to update user with id: {}", id);
        UserResponse updatedUser = userService.updateUser(id, userUpdateRequest);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        log.info("Received request to delete user with id: {}", id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}