package com.group5.taskFlow.service;

import com.group5.taskFlow.dto.UserRegisterResponse;
import com.group5.taskFlow.dto.UserRequest;
import com.group5.taskFlow.dto.UserResponse;
import com.group5.taskFlow.dto.UserUpdateRequest;
import com.group5.taskFlow.exception.EmailAlreadyExistsException;
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
import org.springframework.security.authentication.BadCredentialsException;
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
    private UserRequest userRequest;
    private UserUpdateRequest userUpdateRequest;

    @BeforeEach
    void setUp() {
        userModels = new UserModels();
        userModels.setId(UUID.randomUUID());
        userModels.setEmail("test@example.com");
        userModels.setPasswordHash("encodedPassword");
        userModels.setFirstName("Test");
        userModels.setLastName("User");
        userModels.setRoles(new HashSet<>(Arrays.asList(UserRoles.USER.name())));

        userRequest = new UserRequest();
        userRequest.setEmail("test@example.com");
        userRequest.setPassword("password");
        userRequest.setFirstName("Test");
        userRequest.setLastName("User");
        userRequest.setRoles(new HashSet<>(Collections.singletonList(UserRoles.USER)));

        userUpdateRequest = new UserUpdateRequest();
        userUpdateRequest.setFirstName("Updated");
        userUpdateRequest.setLastName("Name");

        // Manually inject mocks since @InjectMocks might not handle all constructor parameters automatically after adding new ones
        userService = new UserService(userRepository, passwordEncoder, jwtTokenProvider, passwordResetTokenRepository, emailService);

    }

    @Test
    public void registerUser_whenEmailDoesNotExist_shouldSaveUser() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(UserModels.class))).thenReturn(userModels);

        userService.save(userRequest);

        verify(userRepository, times(1)).save(any(UserModels.class));
    }

    @Test
    public void registerUser_whenEmailExists_shouldThrowException() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userModels));

        assertThrows(EmailAlreadyExistsException.class, () -> {
            userService.save(userRequest);
        });

        verify(userRepository, never()).save(any(UserModels.class));
    }

    @Test
    public void authenticateUser_whenCredentialsAreValid_shouldReturnResponseWithToken() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userModels));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtTokenProvider.generateToken(anyString())).thenReturn("test-token");

        UserRegisterResponse response = userService.authenticateUser("test@example.com", "password");

        assertThat(response).isNotNull();
        assertThat(response.email()).isEqualTo("test@example.com");
        assertThat(response.token()).isEqualTo("test-token");
    }

    @Test
    public void authenticateUser_whenEmailNotFound_shouldThrowException() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class, () -> {
            userService.authenticateUser("wrong@example.com", "password");
        });
    }

    @Test
    public void authenticateUser_whenPasswordIsInvalid_shouldThrowException() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userModels));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> {
            userService.authenticateUser("test@example.com", "wrongpassword");
        });
    }

    @Test
    public void updateUser_shouldUpdateOnlyFirstNameAndLastName() {
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(userModels));
        when(userRepository.save(any(UserModels.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse updatedUser = userService.updateUser(userModels.getId(), userUpdateRequest);

        assertThat(updatedUser.getFirstName()).isEqualTo("Updated");
        assertThat(updatedUser.getLastName()).isEqualTo("Name");
        assertThat(updatedUser.getEmail()).isEqualTo("test@example.com"); // Should not change
        verify(userRepository, times(1)).save(any(UserModels.class));
    }

    @Test
    public void save_shouldReturnUserResponse() {
        when(passwordEncoder.encode(userRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(UserModels.class))).thenReturn(userModels);

        UserResponse result = userService.save(userRequest);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(userRequest.getEmail());
        assertThat(result.getFirstName()).isEqualTo(userRequest.getFirstName());
    }

    @Test
    public void getAllUsers_shouldReturnListOfUserResponses() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(userModels));

        List<UserResponse> results = userService.getAllUsers();

        assertThat(results).isNotNull();
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getEmail()).isEqualTo(userModels.getEmail());
    }
    @Test
    void createPasswordResetTokenForUser_whenUserExists_shouldCreateTokenAndSendEmail() {
        // Arrange
        String email = "test@example.com";
        String token = UUID.randomUUID().toString();
        UserModels user = new UserModels();
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordResetTokenRepository.save(any(PasswordResetToken.class))).thenAnswer(invocation -> {
            PasswordResetToken savedToken = invocation.getArgument(0);
            savedToken.setToken(token); // Simulate token generation
            return savedToken;
        });

        // Act
        userService.createPasswordResetTokenForUser(email);

        // Assert
        verify(userRepository, times(1)).findByEmail(email);
        verify(passwordResetTokenRepository, times(1)).save(any(PasswordResetToken.class));
        verify(emailService, times(1)).sendSimpleMessage(eq(email), eq("Password Reset Request"), anyString());
    }

    @Test
    void createPasswordResetTokenForUser_whenUserDoesNotExist_shouldThrowException() {
        // Arrange
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> userService.createPasswordResetTokenForUser(email));

        verify(userRepository, times(1)).findByEmail(email);
        verify(passwordResetTokenRepository, never()).save(any(PasswordResetToken.class));
        verify(emailService, never()).sendSimpleMessage(anyString(), anyString(), anyString());
    }

    @Test
    void resetPassword_whenTokenIsValid_shouldResetPasswordAndDeleteToken() {
        // Arrange
        String token = UUID.randomUUID().toString();
        String newPassword = "newPassword123";
        UserModels user = new UserModels();
        user.setEmail("test@example.com");
        user.setPasswordHash("oldHash");

        PasswordResetToken passwordResetToken = new PasswordResetToken(token, user);
        // Set expiry date to a future date to make it valid
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 10);
        passwordResetToken.setExpiryDate(cal.getTime());

        when(passwordResetTokenRepository.findByToken(token)).thenReturn(passwordResetToken);
        when(passwordEncoder.encode(newPassword)).thenReturn("newEncodedPassword");
        when(userRepository.save(any(UserModels.class))).thenReturn(user);

        // Act
        userService.resetPassword(token, newPassword);

        // Assert
        verify(passwordResetTokenRepository, times(1)).findByToken(token);
        verify(passwordEncoder, times(1)).encode(newPassword);
        verify(userRepository, times(1)).save(user);
        verify(passwordResetTokenRepository, times(1)).delete(passwordResetToken);
        assertThat(user.getPasswordHash()).isEqualTo("newEncodedPassword");
    }

    @Test
    void resetPassword_whenTokenIsInvalid_shouldThrowException() {
        // Arrange
        String token = "invalidToken";
        String newPassword = "newPassword123";

        when(passwordResetTokenRepository.findByToken(token)).thenReturn(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.resetPassword(token, newPassword));

        verify(passwordResetTokenRepository, times(1)).findByToken(token);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(UserModels.class));
        verify(passwordResetTokenRepository, never()).delete(any(PasswordResetToken.class));
    }

    @Test
    void resetPassword_whenTokenIsExpired_shouldThrowException() {
        // Arrange
        String token = UUID.randomUUID().toString();
        String newPassword = "newPassword123";
        UserModels user = new UserModels();
        user.setEmail("test@example.com");

        PasswordResetToken passwordResetToken = new PasswordResetToken(token, user);
        // Set expiry date to a past date to make it expired
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, -10);
        passwordResetToken.setExpiryDate(cal.getTime());

        when(passwordResetTokenRepository.findByToken(token)).thenReturn(passwordResetToken);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.resetPassword(token, newPassword));

        verify(passwordResetTokenRepository, times(1)).findByToken(token);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(UserModels.class));
        verify(passwordResetTokenRepository, never()).delete(any(PasswordResetToken.class));
    }
}
