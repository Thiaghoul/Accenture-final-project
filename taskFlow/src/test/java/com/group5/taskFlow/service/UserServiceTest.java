package com.group5.taskFlow.service;

import com.group5.taskFlow.dto.AuthResponse;
import com.group5.taskFlow.dto.UserRegisterRequest;
import com.group5.taskFlow.dto.UserResponse;
import com.group5.taskFlow.model.PasswordResetToken;
import com.group5.taskFlow.model.UserModels;
import com.group5.taskFlow.model.enums.UserRoles;
import com.group5.taskFlow.repository.PasswordResetTokenRepository;
import com.group5.taskFlow.repository.UserRepository;
import com.group5.taskFlow.security.JwtTokenProvider;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserService userService;

    private UserModels userModels;
    private UserRegisterRequest userRegisterRequest;

    @BeforeEach
    void setUp() {
        userModels = new UserModels();
        userModels.setId(UUID.randomUUID());
        userModels.setEmail("test@example.com");
        userModels.setPasswordHash("encodedPassword");
        userModels.setFirstName("Test");
        userModels.setRoles(new HashSet<>(Collections.singletonList(UserRoles.USER.name())));

        userRegisterRequest = new UserRegisterRequest();
        userRegisterRequest.setFirstName("Test");
        userRegisterRequest.setLastName("User");
        userRegisterRequest.setEmail("test@example.com");
        userRegisterRequest.setPassword("password");
    }

    @Test
    public void registerUser_whenEmailDoesNotExist_shouldSaveUserAndReturnAuthResponse() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(UserModels.class))).thenReturn(userModels);
        when(jwtTokenProvider.generateToken(anyString())).thenReturn("test-token");

        AuthResponse response = userService.registerUser(userRegisterRequest);

        verify(userRepository, times(1)).save(any(UserModels.class));
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("test-token");
        assertThat(response.getUser().getEmail()).isEqualTo(userRegisterRequest.getEmail());
    }

    @Test
    public void registerUser_whenEmailExists_shouldThrowException() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userModels));

        assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser(userRegisterRequest);
        });

        verify(userRepository, never()).save(any(UserModels.class));
    }

    @Test
    public void authenticateUser_whenCredentialsAreValid_shouldReturnAuthResponse() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userModels));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtTokenProvider.generateToken(anyString())).thenReturn("test-token");

        AuthResponse response = userService.authenticateUser("test@example.com", "password");

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("test-token");
        assertThat(response.getUser().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    public void authenticateUser_whenEmailNotFound_shouldThrowException() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            userService.authenticateUser("wrong@example.com", "password");
        });
    }

    @Test
    public void authenticateUser_whenPasswordIsInvalid_shouldThrowException() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userModels));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> {
            userService.authenticateUser("test@example.com", "wrongpassword");
        });
    }

    @Test
    public void getAllUsers_shouldReturnListOfUserResponses() {
        when(userRepository.findAll()).thenReturn(Collections.singletonList(userModels));

        List<UserResponse> results = userService.getAllUsers();

        assertThat(results).isNotNull();
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getEmail()).isEqualTo(userModels.getEmail());
    }

    @Test
    void createPasswordResetTokenForUser_whenUserExists_shouldCreateTokenAndSendEmail() {
        String email = "test@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userModels));

        userService.createPasswordResetTokenForUser(email);

        verify(passwordResetTokenRepository, times(1)).save(any(PasswordResetToken.class));
        verify(emailService, times(1)).sendSimpleMessage(eq(email), eq("Password Reset Request"), anyString());
    }

    @Test
    void createPasswordResetTokenForUser_whenUserDoesNotExist_shouldThrowException() {
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.createPasswordResetTokenForUser(email));
    }

    @Test
    void resetPassword_whenTokenIsValid_shouldResetPasswordAndDeleteToken() {
        String token = UUID.randomUUID().toString();
        String newPassword = "newPassword123";
        PasswordResetToken passwordResetToken = new PasswordResetToken(token, userModels);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 10);
        passwordResetToken.setExpiryDate(cal.getTime());

        when(passwordResetTokenRepository.findByToken(token)).thenReturn(passwordResetToken);
        when(passwordEncoder.encode(newPassword)).thenReturn("newEncodedPassword");

        userService.resetPassword(token, newPassword);

        verify(userRepository, times(1)).save(userModels);
        verify(passwordResetTokenRepository, times(1)).delete(passwordResetToken);
        assertThat(userModels.getPasswordHash()).isEqualTo("newEncodedPassword");
    }

    @Test
    void resetPassword_whenTokenIsInvalid_shouldThrowException() {
        String token = "invalidToken";
        when(passwordResetTokenRepository.findByToken(token)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> userService.resetPassword(token, "newPassword"));
    }

    @Test
    void resetPassword_whenTokenIsExpired_shouldThrowException() {
        String token = UUID.randomUUID().toString();
        PasswordResetToken passwordResetToken = new PasswordResetToken(token, userModels);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, -10);
        passwordResetToken.setExpiryDate(cal.getTime());

        when(passwordResetTokenRepository.findByToken(token)).thenReturn(passwordResetToken);

        assertThrows(IllegalArgumentException.class, () -> userService.resetPassword(token, "newPassword"));
    }
}
