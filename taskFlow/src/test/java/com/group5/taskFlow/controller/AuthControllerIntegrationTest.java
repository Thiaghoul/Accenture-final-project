package com.group5.taskFlow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group5.taskFlow.dto.*;
import com.group5.taskFlow.model.PasswordResetToken;
import com.group5.taskFlow.model.UserModels;
import com.group5.taskFlow.model.enums.UserRoles;
import com.group5.taskFlow.repository.PasswordResetTokenRepository;
import com.group5.taskFlow.repository.UserRepository;
import com.group5.taskFlow.service.EmailService;
import com.group5.taskFlow.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Calendar;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    // Keep other mocks like EmailService if they are used by the service layer during these operations
    @MockBean
    private EmailService emailService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private PasswordResetTokenRepository passwordResetTokenRepository;
    @MockBean
    private PasswordEncoder passwordEncoder;


    private UserModels user;
    private UserResponse userResponse;
    private AuthResponse authResponse;

    @BeforeEach
    void setUp() {
        user = new UserModels();
        user.setId(UUID.randomUUID());
        user.setEmail("test@example.com");
        user.setPasswordHash("encodedPassword");
        user.setFirstName("Test");

        userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setEmail(user.getEmail());
        userResponse.setFirstName(user.getFirstName());
        userResponse.setRoles(Collections.singleton(UserRoles.USER));

        authResponse = new AuthResponse(userResponse, "test-token");
    }

    @Test
    void registerUser_shouldReturnCreated() throws Exception {
        UserRegisterRequest registerRequest = new UserRegisterRequest();
        registerRequest.setFirstName("Test");
        registerRequest.setLastName("User");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password");

        when(userService.registerUser(any(UserRegisterRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("test-token"))
                .andExpect(jsonPath("$.user.email").value("test@example.com"));
    }

    @Test
    void loginUser_shouldReturnOk() throws Exception {
        UserLoginRequest loginRequest = new UserLoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password");

        when(userService.authenticateUser(anyString(), anyString())).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test-token"))
                .andExpect(jsonPath("$.user.email").value("test@example.com"));
    }


    @Test
    void forgotPassword_whenUserExists_shouldReturnOkAndSendEmail() throws Exception {
        PasswordResetRequest request = new PasswordResetRequest();
        request.setEmail("test@example.com");

        // Mocking the service layer call directly
        doNothing().when(userService).createPasswordResetTokenForUser(anyString());

        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(userService, times(1)).createPasswordResetTokenForUser("test@example.com");
    }

    @Test
    void resetPassword_whenTokenIsValid_shouldReturnOkAndResetPassword() throws Exception {
        String token = UUID.randomUUID().toString();
        String newPassword = "newPassword123";
        PasswordResetDto request = new PasswordResetDto();
        request.setToken(token);
        request.setPassword(newPassword);

        doNothing().when(userService).resetPassword(anyString(), anyString());

        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(userService, times(1)).resetPassword(token, newPassword);
    }
}
