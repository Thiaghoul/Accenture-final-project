package com.group5.taskFlow.service;

import com.group5.taskFlow.dto.AuthResponse;
import com.group5.taskFlow.dto.UserRegisterRequest;
import com.group5.taskFlow.dto.UserRegisterResponse;
import com.group5.taskFlow.dto.UserRequest;
import com.group5.taskFlow.dto.UserResponse;
import com.group5.taskFlow.model.PasswordResetToken;
import com.group5.taskFlow.model.UserModels;
import com.group5.taskFlow.model.enums.UserRoles;
import com.group5.taskFlow.repository.PasswordResetTokenRepository;
import com.group5.taskFlow.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import com.group5.taskFlow.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider, PasswordResetTokenRepository passwordResetTokenRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.emailService = emailService;
    }

    public AuthResponse registerUser(UserRegisterRequest request) {
        log.info("Attempting to register user with email: {}", request.getEmail());
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            log.warn("User registration failed: email {} already exists", request.getEmail());
            throw new IllegalArgumentException("User with this email already exists.");
        }

        UserModels userModels = new UserModels();
        userModels.setEmail(request.getEmail());
        userModels.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        userModels.setFirstName(request.getFirstName());
        userModels.setLastName(request.getLastName());
        userModels.setRoles(Collections.singleton(UserRoles.USER.name())); // Default role

        UserModels savedUser = userRepository.save(userModels);
        log.info("User registered successfully with id: {}", savedUser.getId());
        String token = jwtTokenProvider.generateToken(savedUser.getEmail());
        return new AuthResponse(toUserResponse(savedUser), token);
    }

    public AuthResponse authenticateUser(String email, String password) {
        log.info("Attempting to authenticate user with email: {}", email);
        UserModels user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Authentication failed: user not found with email: {}", email);
                    return new IllegalArgumentException("Invalid email or password.");
                });

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            log.warn("Authentication failed: invalid password for user with email: {}", email);
            throw new IllegalArgumentException("Invalid email or password.");
        }

        String token = jwtTokenProvider.generateToken(user.getEmail());
        log.info("User authenticated successfully: {}", email);
        return new AuthResponse(toUserResponse(user), token);
    }

    public UserResponse getCurrentUser(String email) {
        log.info("Fetching current user with email: {}", email);
        UserModels user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", email);
                    return new EntityNotFoundException("User not found with email: " + email);
                });
        return toUserResponse(user);
    }

    public java.util.List<UserResponse> getAllUsers() {
        log.info("Fetching all users");
        java.util.List<UserModels> users = userRepository.findAll();
        log.info("Found {} users", users.size());
        return users.stream()
                .map(this::toUserResponse)
                .collect(Collectors.toList());
    }

    public UserResponse getUserById(UUID id) {
        log.info("Fetching user with id: {}", id);
        UserModels user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found with id: {}", id);
                    return new EntityNotFoundException("User not found with id: " + id);
                });
        return toUserResponse(user);
    }

    public UserResponse updateUser(UUID id, UserRequest userRequest) {
        log.info("Updating user with id: {}", id);
        UserModels existingUser = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found with id: {}", id);
                    return new EntityNotFoundException("User not found with id: " + id);
                });

        existingUser.setEmail(userRequest.getEmail());
        existingUser.setFirstName(userRequest.getFirstName());
        existingUser.setLastName(userRequest.getLastName());
        existingUser.setRoles(userRequest.getRoles().stream().map(UserRoles::name).collect(Collectors.toSet()));

        if (userRequest.getPassword() != null && !userRequest.getPassword().isEmpty()) {
            existingUser.setPasswordHash(passwordEncoder.encode(userRequest.getPassword()));
        }

        UserModels updatedUser = userRepository.save(existingUser);
        log.info("User with id: {} updated successfully", id);
        return toUserResponse(updatedUser);
    }

    public void deleteUser(UUID id) {
        log.info("Deleting user with id: {}", id);
        if (!userRepository.existsById(id)) {
            log.error("User not found with id: {}", id);
            throw new EntityNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
        log.info("User with id: {} deleted successfully", id);
    }

    public void createPasswordResetTokenForUser(String email) {
        log.info("Creating password reset token for user with email: {}", email);
        UserModels user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", email);
                    return new EntityNotFoundException("User not found with email: " + email);
                });
        String token = UUID.randomUUID().toString();
        PasswordResetToken myToken = new PasswordResetToken(token, user);
        passwordResetTokenRepository.save(myToken);
        log.info("Password reset token created for user with email: {}", email);
        emailService.sendSimpleMessage(email, "Password Reset Request", "To reset your password, click the link below:\n" + "http://localhost:8080/reset-password?token=" + token);
    }

    public void resetPassword(String token, String newPassword) {
        log.info("Resetting password with token: {}", token);
        PasswordResetToken passwordResetToken = validatePasswordResetToken(token);
        UserModels user = passwordResetToken.getUser();
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        passwordResetTokenRepository.delete(passwordResetToken);
        log.info("Password for user with email: {} reset successfully", user.getEmail());
    }

    private PasswordResetToken validatePasswordResetToken(String token) {
        log.info("Validating password reset token");
        PasswordResetToken passToken = passwordResetTokenRepository.findByToken(token);
        if (passToken == null) {
            log.error("Invalid password reset token: {}", token);
            throw new IllegalArgumentException("Invalid token.");
        }

        Calendar cal = Calendar.getInstance();
        if ((passToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            log.error("Expired password reset token: {}", token);
            throw new IllegalArgumentException("Token has expired.");
        }
        log.info("Password reset token is valid");
        return passToken;
    }

    private UserResponse toUserResponse(UserModels userModels) {
        UserResponse userResponse = new UserResponse();
        userResponse.setId(userModels.getId());
        userResponse.setEmail(userModels.getEmail());
        userResponse.setFirstName(userModels.getFirstName());
        userResponse.setLastName(userModels.getLastName());
        userResponse.setRoles(userModels.getRoles().stream().map(UserRoles::valueOf).collect(Collectors.toSet()));
        return userResponse;
    }
}