package com.group5.taskFlow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group5.taskFlow.dto.UserLoginRequest;
import com.group5.taskFlow.dto.UserRegisterResponse;
import com.group5.taskFlow.dto.UserRequest;
import com.group5.taskFlow.dto.UserResponse;
import com.group5.taskFlow.security.JwtTokenProvider;
import com.group5.taskFlow.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = UserController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserRequest userRequest;
    private UserResponse userResponse;
    private UserLoginRequest userLoginRequest;
    private UserRegisterResponse userRegisterResponse;

    @BeforeEach
    void setUp() {
        userRequest = new UserRequest();
        userRequest.setEmail("test@example.com");
        userRequest.setPassword("password");
        userRequest.setFirstName("Test");
        userRequest.setLastName("User");
        userRequest.setRoles(new HashSet<>(Collections.singletonList(com.group5.taskFlow.model.enums.UserRoles.USER)));

        userResponse = new UserResponse();
        userResponse.setId(UUID.randomUUID());
        userResponse.setEmail("test@example.com");
        userResponse.setFirstName("Test");
        userResponse.setLastName("User");

        userLoginRequest = new UserLoginRequest();
        userLoginRequest.setEmail("test@example.com");
        userLoginRequest.setPassword("password");

        userRegisterResponse = new UserRegisterResponse("test@example.com", "token");
    }

    @Test
    void registerUser_shouldReturnCreated() throws Exception {
        when(userService.save(any(UserRequest.class))).thenReturn(userResponse);

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(userResponse.getEmail()));
    }

    @Test
    void login_shouldReturnOk() throws Exception {
        when(userService.authenticateUser(userLoginRequest.getEmail(), userLoginRequest.getPassword())).thenReturn(userRegisterResponse);

        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userLoginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(userRegisterResponse.email()))
                .andExpect(jsonPath("$.token").value(userRegisterResponse.token()));
    }

    @Test
    void getAllUsers_shouldReturnOk() throws Exception {
        List<UserResponse> users = Collections.singletonList(userResponse);
        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value(userResponse.getEmail()));
    }

    @Test
    void getUserById_shouldReturnOk() throws Exception {
        when(userService.getUserById(any(UUID.class))).thenReturn(userResponse);

        mockMvc.perform(get("/users/{id}", userResponse.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(userResponse.getEmail()));
    }

    @Test
    void updateUser_shouldReturnOk() throws Exception {
        when(userService.updateUser(any(UUID.class), any(UserRequest.class))).thenReturn(userResponse);

        mockMvc.perform(put("/users/{id}", userResponse.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(userResponse.getEmail()));
    }

    @Test
    void deleteUser_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/users/{id}", UUID.randomUUID()))
                .andExpect(status().isNoContent());
    }
}
