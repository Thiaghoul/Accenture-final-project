package com.group5.taskFlow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group5.taskFlow.dto.PasswordResetDto;
import com.group5.taskFlow.dto.PasswordResetRequest;
import com.group5.taskFlow.model.PasswordResetToken;
import com.group5.taskFlow.model.UserModels;
import com.group5.taskFlow.repository.PasswordResetTokenRepository;
import com.group5.taskFlow.repository.UserRepository;
import com.group5.taskFlow.service.EmailService;
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
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @MockBean
    private EmailService emailService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    private UserModels user;

    @BeforeEach
    void setUp() {
        user = new UserModels();
        user.setId(UUID.randomUUID());
        user.setEmail("test@example.com");
        user.setPasswordHash("oldEncodedPassword");
        user.setFirstName("Test");
        user.setLastName("User");
    }

    @Test
    void forgotPassword_whenUserExists_shouldReturnOkAndSendEmail() throws Exception {
        // Arrange
        PasswordResetRequest request = new PasswordResetRequest();
        request.setEmail("test@example.com");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordResetTokenRepository.save(any(PasswordResetToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act & Assert
        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(passwordResetTokenRepository, times(1)).save(any(PasswordResetToken.class));
        verify(emailService, times(1)).sendSimpleMessage(eq("test@example.com"), eq("Password Reset Request"), anyString());
    }

    @Test
    void forgotPassword_whenUserDoesNotExist_shouldReturnNotFound() throws Exception {
        // Arrange
        PasswordResetRequest request = new PasswordResetRequest();
        request.setEmail("nonexistent@example.com");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound()); // Assuming 404 for user not found

        verify(userRepository, times(1)).findByEmail("nonexistent@example.com");
        verify(passwordResetTokenRepository, never()).save(any(PasswordResetToken.class));
        verify(emailService, never()).sendSimpleMessage(anyString(), anyString(), anyString());
    }

    @Test
    void resetPassword_whenTokenIsValid_shouldReturnOkAndResetPassword() throws Exception {
        // Arrange
        String token = UUID.randomUUID().toString();
        String newPassword = "newPassword123";

        PasswordResetToken passwordResetToken = new PasswordResetToken(token, user);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 10);
        passwordResetToken.setExpiryDate(cal.getTime());

        PasswordResetDto request = new PasswordResetDto();
        request.setToken(token);
        request.setPassword(newPassword);

        when(passwordResetTokenRepository.findByToken(token)).thenReturn(passwordResetToken);
        when(passwordEncoder.encode(newPassword)).thenReturn("newEncodedPassword");
        when(userRepository.save(any(UserModels.class))).thenReturn(user);

        // Act & Assert
        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(passwordResetTokenRepository, times(1)).findByToken(token);
        verify(passwordEncoder, times(1)).encode(newPassword);
        verify(userRepository, times(1)).save(user);
        verify(passwordResetTokenRepository, times(1)).delete(passwordResetToken);
    }

    @Test
    void resetPassword_whenTokenIsInvalid_shouldReturnBadRequest() throws Exception {
        // Arrange
        String token = "invalidToken";
        String newPassword = "newPassword123";

        PasswordResetDto request = new PasswordResetDto();
        request.setToken(token);
        request.setPassword(newPassword);

        when(passwordResetTokenRepository.findByToken(token)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest()); // Assuming 400 for invalid token

        verify(passwordResetTokenRepository, times(1)).findByToken(token);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(UserModels.class));
        verify(passwordResetTokenRepository, never()).delete(any(PasswordResetToken.class));
    }

    @Test
    void resetPassword_whenTokenIsExpired_shouldReturnBadRequest() throws Exception {
        // Arrange
        String token = UUID.randomUUID().toString();
        String newPassword = "newPassword123";

        PasswordResetToken passwordResetToken = new PasswordResetToken(token, user);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, -10);
        passwordResetToken.setExpiryDate(cal.getTime());

        PasswordResetDto request = new PasswordResetDto();
        request.setToken(token);
        request.setPassword(newPassword);

        when(passwordResetTokenRepository.findByToken(token)).thenReturn(passwordResetToken);

        // Act & Assert
        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest()); // Assuming 400 for expired token

        verify(passwordResetTokenRepository, times(1)).findByToken(token);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(UserModels.class));
        verify(passwordResetTokenRepository, never()).delete(any(PasswordResetToken.class));
    }
}
