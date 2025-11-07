package com.group5.taskFlow.service;

import com.group5.taskFlow.dto.*;
import com.group5.taskFlow.exception.EmailAlreadyExistsException;
import com.group5.taskFlow.model.PasswordResetToken;
import com.group5.taskFlow.model.UserModels;
import com.group5.taskFlow.model.enums.UserRoles;
import com.group5.taskFlow.repository.PasswordResetTokenRepository;
import com.group5.taskFlow.repository.UserRepository;
import com.group5.taskFlow.security.JwtTokenProvider;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
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

    public UserResponse save(UserRequest userRequest) {
        log.info("Attempting to save user with email: {}", userRequest.getEmail());
        userRepository.findByEmail(userRequest.getEmail()).ifPresent(user -> {
            log.error("Email {} already exists.", userRequest.getEmail());
            throw new EmailAlreadyExistsException("Email already exists");
        });

        UserModels user = new UserModels();
        user.setEmail(userRequest.getEmail());
        user.setPasswordHash(passwordEncoder.encode(userRequest.getPassword()));
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setRoles(userRequest.getRoles().stream().map(Enum::name).collect(Collectors.toSet()));

        UserModels savedUser = userRepository.save(user);
        log.info("User with email {} saved successfully.", savedUser.getEmail());
        return toUserResponse(savedUser);
    }

    public UserRegisterResponse authenticateUser(String email, String password) {
        log.info("Attempting to authenticate user with email: {}", email);
        UserModels user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("Authentication failed: User not found with email {}", email);
                    return new BadCredentialsException("Invalid credentials");
                });

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            log.error("Authentication failed: Invalid password for user with email {}", email);
            throw new BadCredentialsException("Invalid credentials");
        }

        String token = jwtTokenProvider.generateToken(email);
        log.info("User {} authenticated successfully.", email);
        return new UserRegisterResponse(user.getEmail(), token);
    }

    public List<UserResponse> getAllUsers() {
        log.info("Fetching all users.");
        return userRepository.findAll().stream()
                .map(this::toUserResponse)
                .collect(Collectors.toList());
    }

    public UserResponse getUserById(UUID id) {
        log.info("Fetching user with id: {}", id);
        UserModels user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found with id: {}", id);
                    return new EntityNotFoundException("User not found");
                });
        return toUserResponse(user);
    }

    public UserResponse updateUser(UUID id, UserUpdateRequest userUpdateRequest) {
        log.info("Updating user with id: {}", id);
        UserModels user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found with id: {}", id);
                    return new EntityNotFoundException("User not found");
                });

        user.setFirstName(userUpdateRequest.getFirstName());
        user.setLastName(userUpdateRequest.getLastName());

        UserModels updatedUser = userRepository.save(user);
        log.info("User with id {} updated successfully.", id);
        return toUserResponse(updatedUser);
    }

    public void deleteUser(UUID id) {
        log.info("Deleting user with id: {}", id);
        if (!userRepository.existsById(id)) {
            log.error("User not found with id: {}", id);
            throw new EntityNotFoundException("User not found");
        }
        userRepository.deleteById(id);
        log.info("User with id {} deleted successfully.", id);
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

        String message = "To reset your password, click the link below:\n"
                + "http://localhost:8080/api/auth/reset-password?token=" + token;
        emailService.sendSimpleMessage(user.getEmail(), "Password Reset Request", message);
        log.info("Password reset token created and email sent to {}", email);
    }

    public void resetPassword(String token, String newPassword) {
        log.info("Attempting to reset password with token: {}", token);
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token);
        if (resetToken == null || resetToken.getExpiryDate().before(new Date())) {
            log.error("Invalid or expired password reset token: {}", token);
            throw new IllegalArgumentException("Invalid or expired token");
        }

        UserModels user = resetToken.getUser();
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        passwordResetTokenRepository.delete(resetToken);
        log.info("Password for user {} reset successfully.", user.getEmail());
    }

    private UserResponse toUserResponse(UserModels user) {
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRoles().stream().map(UserRoles::valueOf).collect(Collectors.toSet())
        );
    }
}