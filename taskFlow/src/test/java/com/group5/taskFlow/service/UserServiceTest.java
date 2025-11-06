package com.group5.taskFlow.service;

import com.group5.taskFlow.dto.UserRegisterResponse;
import com.group5.taskFlow.dto.UserRequest;
import com.group5.taskFlow.dto.UserResponse;
import com.group5.taskFlow.model.UserModels;
import com.group5.taskFlow.model.enums.UserRoles;
import com.group5.taskFlow.repository.UserRepository;
import com.group5.taskFlow.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;
import java.util.stream.Collectors;

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

    @InjectMocks
    private UserService userService;

    private UserModels userModels;
    private UserRequest userRequest;

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

        assertThrows(IllegalArgumentException.class, () -> {
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
}
